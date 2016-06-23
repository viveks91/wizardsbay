package com.example.helloworld.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.core.Bid;
import com.example.helloworld.db.BidDAO;
import com.example.helloworld.exception.ResponseException;
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
        if (bidList.isEmpty()) {
            bidDao.create(bid.getId(), bid.getBidder(), bid.getBidAmount());
            Bid existingBid = bidDao.retrieve(bid.getId());
            return existingBid;
        }
        // should get the last index of the list which should be the highest bid
        Bid highest = bidList.get(bidList.size());
        if (bid.getBidAmount() > highest.getBidAmount()) {
            bidDao.create(bid.getId(), bid.getBidder(), bid.getBidAmount());
            Bid existingBid = bidDao.retrieve(bid.getId());
            return existingBid;
        }
        // don't know if this is the right thing to do here.
        return highest;
    }

    //get all bids on an item
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
        return bidDao.findItemById(id);
    }


    //Delete a bid by bid id
    @DELETE
    @Path("/{bidId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public String delete(@PathParam("bidId") int bidId) {
        //> Would we ever need to delete a bid? Maybe delete all bids from a given item after the auction is over
        //> and the buyer has been notified.
//        Bid bid = bidDao.findItemById(bidId);
//        if (bid == null) {
//            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Bid not found");
//        }
//        bidDao.deleteItem(bid);
        return "{}";
    }

}
