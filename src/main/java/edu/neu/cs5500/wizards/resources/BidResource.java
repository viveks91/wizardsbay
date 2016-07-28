package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Bid;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.BidDAO;
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
import java.util.List;

/**
 * Created by susannaedens on 6/21/16.
 */
@Path("/item/{itemId}/bids")
@Api(value = "bids", description = "Operations involving bids on specific item")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BidResource {

    private final BidDAO bidDao;
    private final ItemDAO itemDao;
    private final UserDAO userDao;

    private static final int HIGHEST_BID_INDEX = 0;

    public BidResource(BidDAO bidDao, UserDAO userDao, ItemDAO itemDao) {
        this.bidDao = bidDao;
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    /**
     * Given a bid, if the bid is valid, creates the bid in the database. only if the given bid's bid amount is
     * greater than the current highest bid for the item or if it is the first bid on an item. An error response will
     * sent if: (1) the bidder indicated in the Bid object does not exist; (2) if the bidder indicated in the Bid is
     * not the user attempting to post the bid; (3) if the item to be bid on does not exist; (4) if the bid amount
     * is not higher than the current highest bid for this item.
     *
     * @param incomingBid the bid to create in the database
     * @return a response indicating the success of failure of the creation of this bid
     */
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Creates an bid in the database given the bid", response = Bid.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Bidder does not exist"),
            @ApiResponse(code = HttpStatus.UNAUTHORIZED_401, message = "Error: Invalid credentials"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Item does not exist"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Your bid must be higher than current highest bid: " +
                    "$[highest bid amount]")
    })
    public Response create(@ApiParam(value = "Id of the item to bid on", required = true) @PathParam("itemId") int itemId,
                           @ApiParam(value = "New bid object", required = true) @Valid Bid incomingBid,
                           @ApiParam(hidden = true) @Auth User auth_user) {
        User biddingUser = userDao.retrieve(incomingBid.getBidderUsername());
        if (biddingUser == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Bidder does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!auth_user.equals(biddingUser)) {
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        incomingBid.setBidderId(biddingUser.getId());
        Item item = this.itemDao.findItemById(itemId);
        if (item == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Bid> bidList = this.bidDao.findBidsByItemId(itemId);

        int highestBidAmount = bidList.isEmpty()
                ? item.getMinBidAmount()
                : bidList.get(HIGHEST_BID_INDEX).getBidAmount();

        if (incomingBid.getBidAmount() > highestBidAmount) {
            Bid newBid = this.bidDao.create(itemId, incomingBid.getBidderId(), incomingBid.getBidAmount());
            // update buyer info for the item record
            this.itemDao.updateBuyerInfo(itemId, incomingBid.getBidderId(), incomingBid.getBidAmount());

            return Response.ok(newBid).build();
        } else {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Your bid must be higher than current highest bid: $" + highestBidAmount)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }


    /**
     * Retrieves the history of bids for an item given the item's id.
     *
     * @param itemId the id of the item
     * @return Response containing all bids for a specific item
     */
    @GET
    @Path("/history")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Finds all bids for a specified item given the item id",
            response = Bid.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Item does not exist")
    })
    public Response getAll(@ApiParam(value = "Id of the item", required = true) @PathParam("itemId") int itemId) {
        if (this.itemDao.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Bid> bids = this.bidDao.findBidsByItemId(itemId);
        for (Bid bid : bids) {
            // hide unwanted fields
            bid.setId(null);
            bid.setItemId(null);
        }

        return Response.ok(bids).build();
    }


    /**
     * Retrieve a bid given the bid's id.
     *
     * @param id the id of the bid
     * @return Response containing bid with id matching the given id
     */
    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Finds a bid by it's id", response = Bid.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Item does not exist"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Bid not found"),
            @ApiResponse(code = HttpStatus.FORBIDDEN_403, message = "Forbidden: The bid requested does not belong to the item")
    })
    public Response getOne(@ApiParam(value = "Id of the item", required = true) @PathParam("itemId") int itemId,
                           @ApiParam(value = "Bid Id", required = true) @PathParam("id") int id) {

        if (this.itemDao.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Bid bid = this.bidDao.retrieve(id);
        if (bid == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Bid not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!bid.getItemId().equals(itemId)) {
            return Response
                    .status(HttpStatus.FORBIDDEN_403)
                    .entity("Forbidden: The bid requested does not belong to the item")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        bid.setBidderUsername(this.userDao.retrieveById(bid.getBidderId()).getUsername());
        return Response.ok(bid).build();
    }


    /**
     * Retrieve the highest current bid for an item given the item's id.
     *
     * @param itemId the id of the item
     * @return Response containing highest bid for a specific item
     */
    @GET
    @Path("/highest")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Finds the current highest bid for an item by the item id",
            response = Bid.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Item does not exist"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: There are no bids for this item yet")
    })
    public Response getHighest(@ApiParam(value = "Id of the item", required = true) @PathParam("itemId") int itemId) {

        if (this.itemDao.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Bid> bids = this.bidDao.findBidsByItemId(itemId);
        if (bids.isEmpty()) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: There are no bids for this item yet")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Bid highestBid = bids.get(HIGHEST_BID_INDEX);
        highestBid.setBidderUsername(this.userDao.retrieveById(highestBid.getBidderId()).getUsername());
        highestBid.setItemId(null);

        return Response.ok(highestBid).build();
    }


    /**
     * Given the id of a bid, delete the bid with the matching id from the database. If the bid is not found, throw
     * an exception. If the bid is successfully deleted, return a response indicating success.
     *
     * @param bidId the id of the bid
     * @return Response 204 for successful deletion
     */
    @DELETE
    @Path("/{bidId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Deletes a bid by it's id")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Item does not exist"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Bid does not exist"),
            @ApiResponse(code = HttpStatus.FORBIDDEN_403, message = "Forbidden: The bid requested does not belong to the item"),
            @ApiResponse(code = HttpStatus.UNAUTHORIZED_401, message = "Error: Invalid credentials")
    })
    public Response delete(@ApiParam(value = "Id of the item", required = true) @PathParam("itemId") int itemId,
                           @ApiParam(value = "Id of the bid to be deleted", required = true) @PathParam("bidId") int bidId,
                           @ApiParam(hidden = true) @Auth User auth_user) {

        if (this.itemDao.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Bid bid = this.bidDao.retrieve(bidId);
        if (bid == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Bid does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!bid.getItemId().equals(itemId)) {
            return Response
                    .status(HttpStatus.FORBIDDEN_403)
                    .entity("Forbidden: The bid requested does not belong to the item")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        User biddingUser = this.userDao.retrieveById(bid.getBidderId());
        if (!auth_user.equals(biddingUser)) {
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        bidDao.delete(bid);
        return Response.status(HttpStatus.NO_CONTENT_204).build();
    }
}
