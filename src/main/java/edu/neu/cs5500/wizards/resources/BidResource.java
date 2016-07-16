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
    public Response create(@PathParam("itemId") int itemId, @Valid Bid incomingBid, @Auth User auth_user) {

        User biddingUser = userDao.retrieve(incomingBid.getBidderUsername());
        if(biddingUser == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Bidder does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if(!auth_user.equals(biddingUser)){
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
    public Response getAll(@PathParam("itemId") int itemId) {

        if (this.itemDao.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Bid> bids = this.bidDao.findBidsByItemId(itemId);

        for (Bid bid : bids) {
            // set username
            bid.setBidderUsername(this.userDao.retrieveById(bid.getBidderId()).getUsername());

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
    public Response getOne(@PathParam("itemId") int itemId, @PathParam("id") int id) {

        if (this.itemDao.findItemById(itemId) == null) {
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
                    .entity("Error: Bid not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!bid.getItemId().equals(itemId)) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: The bid requested does not belong to the item")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        bid.setBidderUsername(this.userDao.retrieveById(bid.getBidderId()).getUsername());
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
    public Response getHighest(@PathParam("itemId") int itemId) {

        if (this.itemDao.findItemById(itemId) == null) {
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

        Bid highestBid = bids.get(HIGHEST_BID_INDEX);
        highestBid.setBidderUsername(this.userDao.retrieveById(highestBid.getBidderId()).getUsername());
        highestBid.setItemId(null);

        return Response.ok(highestBid).build();
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
    public Response delete(@PathParam("itemId") int itemId, @PathParam("bidId") int bidId, @Auth User auth_user) {

        if (this.itemDao.findItemById(itemId) == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Item does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Bid bid = this.bidDao.retrieve(bidId);
        if (bid == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Bid does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!bid.getItemId().equals(itemId)) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: The bid requested does not belong to the item")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        User biddingUser = this.userDao.retrieveById(bid.getBidderId());
        if(!auth_user.equals(biddingUser)){
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        bidDao.delete(bid);
        return Response.status(204).build();
    }

}
