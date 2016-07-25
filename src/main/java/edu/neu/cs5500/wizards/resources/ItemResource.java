package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.*;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Path("/item")
@Api(value = "/item", description = "Operations involving items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemDAO itemDao;
    private final UserDAO userDao;

    public ItemResource(ItemDAO itemDao, UserDAO userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    /**
     * Given a valid item and the user making this request, create the item in the database. The item will not be
     * created and an error response will be sent if: (1) the given item is null; (2) If the seller indicated in the
     * item does not exist as a user in the system; (3) if the user attempting to post this item is not the seller
     * indicated by the field in the item; (4) if the indicated end time for the auction is set for some time in the
     * past.
     *
     * @param item      the item to create in the database
     * @param auth_user the user requesting to create the item
     * @return a response indicating success or failure of posting the item
     */
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Creates an item in the database given that item",
            response = Item.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Invalid item"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Seller does not exist"),
            @ApiResponse(code = HttpStatus.UNAUTHORIZED_401, message = "Error: Invalid credentials"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Invalid auction end time")
    })
    public Response create(@ApiParam(value = "Item object to be created", required = true) @Valid Item item,
                           @ApiParam(hidden = true) @Auth User auth_user) {
        if (item == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Invalid item")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        User itemSeller = this.userDao.retrieve(item.getSellerUsername());
        if (itemSeller == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Seller does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!auth_user.equals(itemSeller)) {
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Date now = new Date();
        if (item.getAuctionEndTime().before(now)) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Invalid auction end time")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        item.setSellerId(itemSeller.getId());
        Item createdItem = this.itemDao.create(item);
        return Response.ok(createdItem).build();
    }

    //getActive all items listed by a seller
    // added this to user/{username}/items
//    @GET
//    @Path("/seller/{sellerId}")
//    @Timed
//    @UnitOfWork
//    @ExceptionMetered
//    public Response getActive(@PathParam("sellerId") int sellerId) {
//        return Response.ok(itemDao.findItemsBySellerId(sellerId)).build();
//    }


    /**
     * Given an item, update the item in the database. The item will not be updated and an error response will be
     * sent under the following circumstances: (1) the given item is null; (2) the item does not exist in the database
     * to begin with; (3) the request is attempting to change the seller for the item; (4) The request is attempting
     * to change the id of the item; (5) the user attempting to change the item is not the seller of the item.
     *
     * @param id        the id of the item to update
     * @param item      the object containing the new information
     * @param auth_user the user attempting to update the object
     * @return a response indicating success or failure of the update and return the updated object
     */
    @PUT
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Updates an item in the database given the item with updated information",
            response = Item.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Invalid item"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Item not found, update failed"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Seller cannot be changed"),
            @ApiResponse(code = HttpStatus.UNAUTHORIZED_401, message = "Error: Invalid credentials"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Item id cannot be changed"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Auction end time cannot be changed, since it has already passed"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Minimum bid amount cannot be less than $1")
    })
    public Response update(@ApiParam(value = "id of the item to be updated", required = true) @PathParam("id") int id,
                           @ApiParam(value = "Updated item object", required = true) Item item,
                           @ApiParam(hidden = true) @Auth User auth_user) {
        if (item == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Invalid item")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        Item existingItem = this.itemDao.findItemById(id);
        if (existingItem == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Item not found, update failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        User itemSeller = this.userDao.retrieveById(existingItem.getSellerId());
        if (item.getSellerUsername() != null) {
            if (!item.getSellerUsername().equals(itemSeller.getUsername())) {
                return Response
                        .status(HttpStatus.BAD_REQUEST_400)
                        .entity("Error: Seller cannot be changed")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }
        if (!auth_user.equals(itemSeller)) {
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        if (item.getId() != null) {
            if (!existingItem.getId().equals(item.getId())) {
                return Response
                        .status(HttpStatus.BAD_REQUEST_400)
                        .entity("Error: Item id cannot be changed")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }
        if (item.getAuctionEndTime() != null) {
            Date now = new Date();
            if (!item.getAuctionEndTime().equals(existingItem.getAuctionEndTime())
                    && existingItem.getAuctionEndTime().before(now)) {
                return Response
                        .status(HttpStatus.BAD_REQUEST_400)
                        .entity("Error: Auction end time cannot be changed, since it has already passed")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            } else {
                existingItem.setAuctionEndTime(item.getAuctionEndTime());
            }
        }
        if (item.getMinBidAmount() != null) {
            if (item.getMinBidAmount() < 1) {
                return Response
                        .status(HttpStatus.BAD_REQUEST_400)
                        .entity("Error: Minimum bid amount cannot be less than $1")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            } else {
                existingItem.setMinBidAmount(item.getMinBidAmount());
            }
        }
        if (item.getItemName() != null) {
            existingItem.setItemName(item.getItemName());
        }
        if (item.getItemDescription() != null) {
            existingItem.setItemDescription(item.getItemDescription());
        }
        this.itemDao.update(existingItem.getId(), existingItem.getItemName(), existingItem.getItemDescription(),
                existingItem.getAuctionEndTime(), existingItem.getMinBidAmount());
        return Response.ok(existingItem).build();
    }


    /**
     * Returns all active items in the database. Active items are those that are still available to be bid on.
     *
     * @return all active items
     */
    @GET
    @Path("/active")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Finds all active items in the database",
            response = Item.class,
            responseContainer = "List")
    public Response getActive() {
        List<Item> activeItems = this.itemDao.findAllActiveItems();
        for (Item item : activeItems) {
            //hide some details
            item.setAuctionStartTime(null);
            // set seller username
            item.setSellerUsername(this.userDao.retrieveById(item.getSellerId()).getUsername());
        }
        return Response.ok(activeItems).build();
    }


    /**
     * Given an id corresponding to an item, return that item from the database. If the item is not found, send an
     * error response code.
     *
     * @param id the id of the item to return
     * @return the id corresponding to the given id
     */
    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Finds an item by id", response = Item.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Item not found")
    })
    public Response getOne(@ApiParam(value = "Id of the item", required = true) @PathParam("id") int id) {
        Item item = this.itemDao.findItemById(id);
        if (item == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Item not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        item.setSellerUsername(this.userDao.retrieveById(item.getSellerId()).getUsername());
        if (item.getBuyerId() != 0) { // sold or has a current highest bidder
            item.setBuyerUsername(this.userDao.retrieveById(item.getBuyerId()).getUsername());
        }
        return Response.ok(item).build();
    }


    /**
     * Given the id of an item, delete that item from the database. The item will not be deleted and an error response
     * will be sent if: (1) The id does not correspond to an existing item; (2) If the user attempting to delete the
     * item is not the seller of the item.
     *
     * @param itemId    the id of the item to delete
     * @param auth_user the user requesting to delete the item
     * @return a response code indicating success or failure
     */
    @DELETE
    @Path("/{itemId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Deletes an item from the database by id")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Item not found"),
            @ApiResponse(code = HttpStatus.UNAUTHORIZED_401, message = "Error: Invalid credentials")
    })
    public Response delete(@ApiParam(value = "Id of the item to be deleted", required = true) @PathParam("itemId") int itemId,
                           @ApiParam(hidden = true) @Auth User auth_user) {
        Item item = this.itemDao.findItemById(itemId);

        if (item == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Item not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        User itemSeller = this.userDao.retrieveById(item.getSellerId());
        if (!auth_user.equals(itemSeller)) {
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        this.itemDao.deleteItem(itemId);
        return Response.status(HttpStatus.NO_CONTENT_204).build();
    }
}
