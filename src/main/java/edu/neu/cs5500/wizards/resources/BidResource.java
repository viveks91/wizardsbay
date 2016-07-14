package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Bid;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.db.BidDAO;
import edu.neu.cs5500.wizards.db.ItemDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by susannaedens on 6/21/16.
 */
@Path("/item/{itemId}/bids")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BidResource {

    private final BidDAO bidDao;
    private final ItemDAO itemDAO;

    private static final int HIGHEST_BID_INDEX = 0;

    public BidResource(BidDAO bidDao, ItemDAO itemDAO) {
        this.bidDao = bidDao;
        this.itemDAO = itemDAO;
    }

    private List<Bid> getAllBidsForItemId(int itemId) throws IllegalArgumentException {
        if (this.itemDAO.findItemById(itemId) == null) {
            throw new IllegalArgumentException("Item does not exist");
        }
        return this.bidDao.findBidsByItemId(itemId);
    }

    /**
     * Creates a bid and returns it only if the given bid's bid amount is greater than the current highest bid for
     * the item or if it is the first bid on an item.
     *
     * @param incomingBid the bid to create and return
     * @return Response containing the new bid.
     */
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response post(@PathParam("itemId") int itemId, Bid incomingBid) {

        Item item = this.itemDAO.findItemById(itemId);
        if (item == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Bid> bidList = this.bidDao.findBidsByItemId(itemId);

        int highestBidAmount = bidList.isEmpty()
                ? item.getMinBidAmount()
                : bidList.get(HIGHEST_BID_INDEX).getBidAmount();

        if (incomingBid.getBidAmount() > highestBidAmount) {
            int newBidId = this.bidDao.create(itemId, incomingBid.getBidder(), incomingBid.getBidAmount());
            incomingBid.setId(newBidId);
            return Response.ok(incomingBid).build();
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
    public Response get(@PathParam("itemId") int itemId) {

        if (this.itemDAO.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        return Response.ok(this.bidDao.findBidsByItemId(itemId)).build();
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
    public Response getById(@PathParam("itemId") int itemId, @PathParam("id") int id) {

        if (this.itemDAO.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Bid bid = this.bidDao.retrieve(id);
        if (bid == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Bid does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        return Response.ok(bid).build();
    }


    /**
     * Retrieve the highest current bid for an item given the item's id.
     *
     * @param itemId
     * @return Response containing highest bid for a specific item
     */
    @GET
    @Path("/highest")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response getHighestBid(@PathParam("itemId") int itemId) {

        if (this.itemDAO.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Bid> bids = this.bidDao.findBidsByItemId(itemId);
        if (bids.isEmpty()) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: There are no bids for this item yet")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        return Response.ok(bids.get(HIGHEST_BID_INDEX)).build();
    }


    /**
     * Given the id of a bid, delete the bid with the matching id from the database. If the bid is not found, throw
     * an exception. If the bid is successfully deleted, return a 204 response code.
     *
     * @param bidId the id of the bid
     * @return Response 204 for successful deletion
     */
    @DELETE
    @Path("/{bidId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response delete(@PathParam("itemId") int itemId, @PathParam("bidId") int bidId) {

        if (this.itemDAO.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Bid bid = bidDao.retrieve(bidId);
        if (bid == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Bid does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        bidDao.delete(bid);
        return Response.status(204).build();
    }

}
