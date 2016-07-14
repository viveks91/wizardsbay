package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.db.ItemDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/item")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {

    private final ItemDAO itemDao;


    public ItemResource(ItemDAO itemDao) {
        this.itemDao = itemDao;
    }

    //Create a listing
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response post(Item item) {
        if( item == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Invalid item")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Item createdItem = itemDao.create(item);
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
        return Response.ok(itemDao.findAllActiveItems()).build();
    }

    //get item by id

    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response getById(@PathParam("id") int id) {
        return Response.ok(itemDao.findItemById(id)).build();
    }

    //Delete an item
    @DELETE
    @Path("/{itemId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    /* delete user by username*/
    public Response delete(@PathParam("itemId") int itemId) {
        Item item = itemDao.findItemById(itemId);
        if (item == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        itemDao.deleteItem(item);

        return Response.status(HttpStatus.NO_CONTENT_204).build();
    }
}
