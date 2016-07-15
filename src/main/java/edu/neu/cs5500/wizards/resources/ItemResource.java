package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.UserDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Path("/item")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemDAO itemDao;
    private final UserDAO userDao;

    public ItemResource(ItemDAO itemDao, UserDAO userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    //Create a listing
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response post(Item item, @Auth User auth_user) {
        if( item == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Invalid item")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        User itemSeller = this.userDao.retrieveById(item.getSellerId());
        if(itemSeller == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Seller does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if(!auth_user.equals(itemSeller)){
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if(item.getMinBidAmount() < 1) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Minimum bid amount cannot be less than $1")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Timestamp current = new Timestamp(new Date().getTime());

        if(item.getAuctionEndTime().before(item.getAuctionStartTime()) ||
                item.getAuctionEndTime().before(current)) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Invalid auction end time")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Item createdItem = this.itemDao.create(item);
        return Response.ok(createdItem).build();
    }

    //get all items listed by a seller
    // added this to user/{username}/items
//    @GET
//    @Path("/seller/{sellerId}")
//    @Timed
//    @UnitOfWork
//    @ExceptionMetered
//    public Response get(@PathParam("sellerId") int sellerId) {
//        return Response.ok(itemDao.findItemsBySellerId(sellerId)).build();
//    }

    //get all active items
    @GET
    @Path("/active")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response get() {
        List<Item> activeItems = this.itemDao.findAllActiveItems();

        //hide some details
        for (Item item : activeItems) {
            item.setAuctionStartTime(null);
            item.setBuyerId(null);
        }

        return Response.ok(activeItems).build();
    }

    //get item by id

    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response getById(@PathParam("id") int id) {
        Item item = this.itemDao.findItemById(id);
        if (item == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (item.getBuyerId() == 0) { // not sold
            item.setBuyerId(null);
        }
        return Response.ok(item).build();
    }

    //Delete an item
    @DELETE
    @Path("/{itemId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    /* delete user by username*/
    public Response delete(@PathParam("itemId") int itemId, @Auth User auth_user) {
        Item item = this.itemDao.findItemById(itemId);

        if (item == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        User itemSeller = this.userDao.retrieveById(item.getSellerId());
        if(!auth_user.equals(itemSeller)){
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
