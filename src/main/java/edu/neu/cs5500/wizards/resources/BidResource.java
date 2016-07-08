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

    //Create bid only if given bid is higher than the current bid on the item
    // OR if it is the first bid on an item
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

    //get all bids for a given item
    @GET
    @Path("/history/{itemId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public List<Bid> get(@PathParam("itemId") int itemId) {
        return bidDao.findBidsByItemId(itemId);
    }


    //get bid by id
    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Bid getById(@PathParam("id") int id) {
        return bidDao.retrieve(id);
    }


    //get highest bid for an item given itemId
    @GET
    @Path("/item/{itemId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Bid getByItemId(@PathParam("itemId") int itemId) {
        Bid highest = bidDao.retrieveHighestBid(itemId);
        return highest;
    }


    //Delete a bid by bid id
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
