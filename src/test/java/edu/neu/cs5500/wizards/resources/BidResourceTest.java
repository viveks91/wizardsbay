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
    Item item;

    @Mock
    Bid bid;

    Random rand = new Random();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(auth_user.getUsername()).thenReturn(RandomStringUtils.random(5));
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        when(userDAO.retrieve(anyString())).thenReturn(auth_user);
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
    }


    @Test
    public void testPostingWithInvalidBidder() throws Exception {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        when(bid.getBidderUsername()).thenReturn(RandomStringUtils.random(5));
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.create(rand.nextInt(), bid, auth_user);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Bidder does not exist", response.getEntity());
    }

    @Test
    public void testPostingWithUnauthorizedUser() throws Exception {
        when(userDAO.retrieve(anyString())).thenReturn(Mockito.mock(User.class));
        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);

        Response response = bidResource.create(rand.nextInt(), bid, auth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

//    @Test
//    public void testPostingWithInvalidItem() throws Exception {
//        when(userDAO.retrieve(anyString())).thenReturn(Mockito.mock(User.class));
//        BidResource bidResource = new BidResource(bidDAO, userDAO, itemDAO);
//
//        Response response = bidResource.create(rand.nextInt(), bid, auth_user);
//        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
//        assertEquals("Error: Invalid credentials", response.getEntity());
 //   }

    @Test
    public void testCreate() throws Exception {
        // if item for bid does not exist
        // if bid is less than current highest bid

        // if bid list is empty -> post new bid
        // if bid is higher than current highest bid -> post bid

    }



    @Test
    // returns bid history for given item
    public void testGetAll() throws Exception {
        // if item does not exist
        // if item exists, and no bids -> empty list
        // if item exists and there are bids -> list of bids
    }

    @Test
    // returns bid given id
    public void testGetOne() throws Exception {
        // if item does not exist
        // if bid does not exist
        // if the bid does not belong to that item

        // if bid exists, return bid
    }

    @Test
    public void getHighestBid() throws Exception {
       // if item does not exist
        // if there are no bids for the item yet

        // return highest bid
    }

    @Test
    public void delete() throws Exception {
        // if item does not exist
        // if bid does not exist
        // if the bid does not belong to the specified item
        // if invalid user

        // delete the item
    }

}