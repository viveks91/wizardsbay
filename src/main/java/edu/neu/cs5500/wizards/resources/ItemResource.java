package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.UserDAO;
import edu.neu.cs5500.wizards.exception.ResponseException;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public Item post(Item item) {
        if( item == null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Invalid item");
        }

        item.setCurrentMaxBid(item.getMinBidAmount());
        Item createdItem = itemDao.create(item);

        return createdItem;
    }

    //get all items listed by a seller
    @GET
    @Path("/seller/{sellerId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public List<Item> get(@PathParam("sellerId") int sellerId) {
        return itemDao.findItemBySellerId(sellerId);
    }

    //get all active items
    @GET
    @Path("/active")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public List<Item> get() {
        return itemDao.findAllActiveItems();
    }

    //get item by id

    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Item getById(@PathParam("id") int id) {
        return itemDao.findItemById(id);
    }

    //Delete an item
    @DELETE
    @Path("/{itemId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    /* delete user by username*/
    public Response delete(@PathParam("itemId") int itemId, @Auth User auth_user) {
        Item item = itemDao.findItemById(itemId);
        User itemSeller = userDao.retrieveById(item.getSellerId());
        if(!auth_user.equals(itemSeller)){
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Not Authorized");
        }
        
        if (item == null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Item not found");
        }
        itemDao.deleteItem(item);

        return Response.status(204).build();
    }


}
