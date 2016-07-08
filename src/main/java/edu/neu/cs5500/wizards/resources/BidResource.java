package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Bid;
import edu.neu.cs5500.wizards.db.BidDAO;
import edu.neu.cs5500.wizards.exception.ResponseException;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by susannaedens on 6/21/16.
 */
@Path("/bids")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BidResource {

    private final BidDAO bidDao;

    public BidResource(BidDAO bidDao) {
        this.bidDao = bidDao;
    }

    /**
     * Creates a bid and returns it only if the given bid's bid amount is greater than the current highest bid for
     * the item or if it is the first bid on an item.
     *
     * @param bid the bid to create and return
     * @return the new bid.
     */
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Bid post(Bid bid) {
        List<Bid> bidList = get(bid.getItemId());
        Bid newBid = new Bid();
        if (bidList.isEmpty()) {
            bidDao.create(bid.getItemId(), bid.getBidder(), bid.getBidAmount());
            newBid = bidDao.retrieve(bid.getId());
        } else {
            Bid highest = bidDao.retrieveHighestBid(bid.getItemId());
            if (bid.getBidAmount() > highest.getBidAmount()) {
                bidDao.create(bid.getItemId(), bid.getBidder(), bid.getBidAmount());
                newBid = bidDao.retrieve(bid.getId());
            } else {
                ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Your bid must be higher than current " +
                        "highest bid: $" + highest.getBidAmount());
            }
        }
        return newBid;
    }


    /**
     * Retrieves the history of bids for an item given the item's id.
     *
     * @param itemId the id of the item
     * @return all bids for a specific item
     */
    @GET
    @Path("/history/{itemId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public List<Bid> get(@PathParam("itemId") int itemId) {
        return bidDao.findBidsByItemId(itemId);
    }


    /**
     * Retrieve a bid given the bid's id.
     *
     * @param id the id of the bid
     * @return the bid with the id matching the given id
     */
    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Bid getById(@PathParam("id") int id) {
        return bidDao.retrieve(id);
    }


    /**
     * Retrieve the highest current bid for an item given the item's id.
     *
     * @param itemId
     * @return
     */
    @GET
    @Path("/item/{itemId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Bid getHighestBid(@PathParam("itemId") int itemId) {
        Bid highest = bidDao.retrieveHighestBid(itemId);
        if (highest == null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "There are no bids for this item yet.");
        }
        return highest;
    }


    /**
     * Given the id of a bid, delete the bid with the matching id from the database.
     *
     * @param bidId the id of the bid
     * @return an string representing an empty set
     */
    @DELETE
    @Path("/{bidId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public String delete(@PathParam("bidId") int bidId) {
        Bid bid = bidDao.retrieve(bidId);
        if (bid == null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Bid not found");
        }
        bidDao.delete(bid);
        return "{}";
    }

}
