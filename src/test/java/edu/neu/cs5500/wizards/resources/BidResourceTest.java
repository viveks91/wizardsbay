package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.Bid;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.BidDAO;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by susannaedens on 7/8/16.
 */
public class BidResourceTest {

    @Mock
    BidDAO bidDAO;

    @Mock
    ItemDAO itemDAO;

    @Mock
    UserDAO userDAO;

    @Mock
    User auth_user;

    @Mock
    User unauth_user;

    @Mock
    Item item;

    @Mock
    Bid bid;

    @Mock
    Bid highBid;

    @Mock
    List<Bid> bidList;

    Random rand = new Random();
    static final Timestamp THE_PAST = new Timestamp(1470441600021L);
    static final Timestamp THE_FUTURE = new Timestamp(System.currentTimeMillis() + 1000000L);

    List<Bid> bidListGetAll;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(auth_user.getUsername()).thenReturn(RandomStringUtils.random(5));
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        when(userDAO.retrieve(anyString())).thenReturn(auth_user);
        when(itemDAO.findItemById(anyInt())).thenReturn(item);

        bidListGetAll = new LinkedList<>();
        bidListGetAll.add(bid);
    }


    @Test
    // when the bidder does not exist
    public void testPostingWithInvalidBidder() throws Exception {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        when(bid.getBidderUsername()).thenReturn(RandomStringUtils.random(5));
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.create(rand.nextInt(), bid, auth_user);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Bidder does not exist", response.getEntity());
    }

    @Test
    // when the user is unauthorized to post this bid
    public void testPostingWithUnauthorizedUser() throws Exception {
        when(userDAO.retrieve(anyString())).thenReturn(Mockito.mock(User.class));
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.create(rand.nextInt(), bid, unauth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

    @Test
    // when the item to be bid on does not exist
    public void testPostingWithInvalidItem() throws Exception {
        Integer itemId = rand.nextInt();
        when(itemDAO.findItemById(itemId)).thenReturn(null);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.create(itemId, bid, auth_user);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Item does not exist", response.getEntity());
    }

    @Test
    // when the auction for the item has already ended
    public void testPostingWhenAuctionOver() throws Exception {
        Item over = new Item();
        //> timestamp for august 5th, 2016 at 3pm
        over.setAuctionEndTime(THE_PAST);
        when(itemDAO.findItemById(anyInt())).thenReturn(over);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.create(rand.nextInt(), bid, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: The auction for this item has ended", response.getEntity());
    }


    @Test
    // when the incoming bid is not greater than the existing highest bid
    public void testPostingWhenLessThanMinBid() throws Exception {
        // when the bid list is empty
        when(item.getMinBidAmount()).thenReturn(5);
        when(item.getAuctionEndTime()).thenReturn(THE_FUTURE);
        when(bidDAO.findBidsByItemId(anyInt())).thenReturn(new LinkedList<Bid>());
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        when(bid.getBidAmount()).thenReturn(1);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.create(rand.nextInt(), bid, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Your bid must be higher than current highest bid: $5", response.getEntity());

        // when the bid list isn't empty
        when(bidList.get(0)).thenReturn(highBid);
        when(highBid.getBidAmount()).thenReturn(30);
        when(bidDAO.findBidsByItemId(anyInt())).thenReturn(bidList);

        Response response2 = bidResource.create(rand.nextInt(), bid, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response2.getStatus());
        assertEquals("Error: Your bid must be higher than current highest bid: $30", response2.getEntity());
    }


    @Test
    public void testValidPosting() throws Exception {
        // when the bid list is empty
        when(item.getMinBidAmount()).thenReturn(5);
        when(item.getAuctionEndTime()).thenReturn(THE_FUTURE);
        when(bidDAO.findBidsByItemId(anyInt())).thenReturn(new LinkedList<Bid>());
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        when(bid.getBidAmount()).thenReturn(36);
        when(bidDAO.create(anyInt(), anyInt(), anyInt())).thenReturn(bid);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.create(rand.nextInt(), bid, auth_user);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(bid, response.getEntity());

        // when the bid list isn't empty
        when(bidList.get(0)).thenReturn(highBid);
        when(highBid.getBidAmount()).thenReturn(30);
        when(bidDAO.findBidsByItemId(anyInt())).thenReturn(bidList);

        Response response2 = bidResource.create(rand.nextInt(), bid, auth_user);
        assertEquals(HttpStatus.OK_200, response2.getStatus());
        assertEquals(bid, response2.getEntity());
    }


    @Test
    // get all bids when the item does not exist
    public void testGetAllWhenInvalidItem() throws Exception {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getAll(rand.nextInt());
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Item does not exist", response.getEntity());
    }


    @Test
    // returns bid history for given item
    public void testGetAll() throws Exception {
        when(bidDAO.findBidsByItemId(anyInt())).thenReturn(new LinkedList<Bid>());
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getAll(rand.nextInt());
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(new LinkedList<Bid>(), response.getEntity());

        // when bid list is not empty
        when(bidDAO.findBidsByItemId(anyInt())).thenReturn(bidListGetAll);

        Response response2 = bidResource.getAll(rand.nextInt());
        assertEquals(HttpStatus.OK_200, response2.getStatus());
        assertEquals(bidListGetAll, response2.getEntity());
    }


    @Test
    // get one when item does not exist
    public void testGetOneInvalidItem() throws Exception {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getOne(rand.nextInt(), rand.nextInt());
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Item does not exist", response.getEntity());
    }

    @Test
    // get one when request bid does not exist
    public void testGetOneInvalidBid() throws Exception {
        when(bidDAO.retrieve(anyInt())).thenReturn(null);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getOne(rand.nextInt(), rand.nextInt());
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Bid not found", response.getEntity());
    }

    @Test
    // get one when requested bid's item id does not match the item id
    public void testGetOneItemBidNoMatch() throws Exception {
        when(bidDAO.retrieve(anyInt())).thenReturn(bid);
        Integer itemId = rand.nextInt();
        when(bid.getItemId()).thenReturn(rand.nextInt());
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getOne(itemId, rand.nextInt());
        assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
        assertEquals("Forbidden: The bid requested does not belong to the item", response.getEntity());
    }

    @Test
    // returns bid given id
    public void testGetOne() throws Exception {
        when(bidDAO.retrieve(anyInt())).thenReturn(bid);
        Integer itemId = rand.nextInt();
        when(bid.getItemId()).thenReturn(itemId);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getOne(itemId, rand.nextInt());
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(bid, response.getEntity());
    }

    @Test
    // get highest bid when item doesnt exist
    public void getHighestBidInvalidItem() throws Exception {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getHighest(rand.nextInt());
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Item does not exist", response.getEntity());
    }

    @Test
    // get highest bid when there are no bids on the item yet
    public void getHighestBidEmptyBidList() throws Exception {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        when(bidDAO.findBidsByItemId(anyInt())).thenReturn(new LinkedList<Bid>());
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getHighest(rand.nextInt());
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: There are no bids for this item yet", response.getEntity());
    }

    @Test
    // get the highest current bid for an item
    public void getHighestBid() throws Exception {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        when(bidDAO.findBidsByItemId(anyInt())).thenReturn(bidList);
        when(bidList.get(0)).thenReturn(bid);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.getHighest(rand.nextInt());
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(bid, response.getEntity());
    }

    @Test
    // test delete when item doesn't exist
    public void deleteInvalidItem() throws Exception {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.delete(rand.nextInt(), rand.nextInt(), auth_user);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Item does not exist", response.getEntity());
    }

    @Test
    // test delete when bid doesn't exist
    public void deleteInvalidBid() throws Exception {
        when(bidDAO.retrieve(anyInt())).thenReturn(null);
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.delete(rand.nextInt(), rand.nextInt(), auth_user);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Bid does not exist", response.getEntity());
    }

    @Test
    // test delete when bid does not match the item
    public void deleteItemBidNoMatch() throws Exception {
        when(bidDAO.retrieve(anyInt())).thenReturn(bid);
        Integer itemId = rand.nextInt();
        when(bid.getItemId()).thenReturn(rand.nextInt());
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.delete(itemId, rand.nextInt(), auth_user);
        assertEquals(HttpStatus.FORBIDDEN_403, response.getStatus());
        assertEquals("Forbidden: The bid requested does not belong to the item", response.getEntity());
    }

    @Test
    // test delete when user is unauthorized
    public void testdeleteWithUnauthorizedUser() throws Exception {
        when(bidDAO.retrieve(anyInt())).thenReturn(bid);
        Integer itemId = rand.nextInt();
        when(bid.getItemId()).thenReturn(itemId);
        when(userDAO.retrieve(anyString())).thenReturn(Mockito.mock(User.class));
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.delete(itemId, rand.nextInt(), unauth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

    @Test
    public void testDelete() throws Exception {
        when(bidDAO.retrieve(anyInt())).thenReturn(bid);
        Integer itemId = rand.nextInt();
        when(bid.getItemId()).thenReturn(itemId);
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.delete(itemId, rand.nextInt(), auth_user);
        assertEquals(HttpStatus.NO_CONTENT_204, response.getStatus());
        assertEquals(null, response.getEntity());
    }

}