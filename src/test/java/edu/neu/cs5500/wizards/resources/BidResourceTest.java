package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.Bid;
import edu.neu.cs5500.wizards.db.BidDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by susannaedens on 7/8/16.
 */
public class BidResourceTest {

    // mock bidDao and Bid for testing without actually calling to database or creating objects
    @Mock
    BidDAO bidDAO;

    @Mock
    Bid bid;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void post() throws Exception {
        //1. if bid list is empty
        //2. if bid list is not empty and given bid is higher than current bid
        //3. (test exception) bid list is not empty but given bid is lower than current bid

        //> logic needed:
        //1. If falls between appropriate time for bidding (post) if not, (exception)
        //2. If first bid, is higher than starting price for item (post) if not, (exception)
    }

    @Test
    // returns bid history for given item
    public void get() throws Exception {
        //1. if there is no bid history -> return empty list
        //2. if there is a bid history -> return list of bids

        //> logic needed
        //1. if item does not exist (exception)
    }

    @Test
    // returns bid given id
    public void getById() throws Exception {
        //1. if bid exists, return bid

        //> logic needed
        //1. if item does not exist (exception)
    }

    @Test
    public void getHighestBid() throws Exception {
        //1. if there are existing bids, return highest
        //2. if no bids yet -> (exception)
    }

    @Test
    public void delete() throws Exception {
        //1. if bid exists, delete and return response code
        //2. if does not exist, (exception)

        //> logic needed
        //1. Can only delete bid if admin or if user who created the bid?
    }

}