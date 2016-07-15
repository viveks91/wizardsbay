package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.ItemDAO;
import org.eclipse.jetty.http.HttpStatus;
import edu.neu.cs5500.wizards.db.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class ItemResourceTest {

    @Mock
    ItemDAO itemDAO;
    
    @Mock
    UserDAO userDAO;

    @Mock
    User auth_user;

    @Mock
    Item item;

    // This function gets invoked before each of the tests below
    @Before
    public void before() {
        userDAO = Mockito.mock(UserDAO.class);
        itemDAO = Mockito.mock(ItemDAO.class);
        item = Mockito.mock(Item.class);
        auth_user = Mockito.mock(User.class);
    }

    @Test
    public void testItemCreation() {
        String dummyStartTime = "1990-01-01 11:11:11";
        String dummyEndTime = "2100-01-01 11:11:11";
        int numberMoreThanZero = (int)Math.random() + 1 ;
        when(item.getSellerId()).thenReturn((int)Math.random());
        when(item.getMinBidAmount()).thenReturn(numberMoreThanZero);
        when(item.getAuctionStartTime()).thenReturn(Timestamp.valueOf(dummyStartTime));
        when(item.getAuctionEndTime()).thenReturn(Timestamp.valueOf(dummyEndTime));
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        when(itemDAO.create(any(Item.class))).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.post(item, auth_user);
        assertEquals(response.getEntity(), item);
    }

    @Test
    public void testExceptionOnPostingItemWhenContentIsNull() {
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        Response response = itemResource.post(null, auth_user);
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST_400);
        assertEquals(response.getEntity(), "Error: Invalid item");
    }

    @Test
    public void testExceptionOnPostingItemWithInvalidCredentials() {
        when(userDAO.retrieveById(anyInt())).thenReturn(Mockito.mock(User.class));
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        Response response = itemResource.post(item, auth_user);
        assertEquals(response.getStatus(), HttpStatus.UNAUTHORIZED_401);
        assertEquals(response.getEntity(), "Error: Invalid credentials");
    }

    @Test
    public void testExceptionOnPostingItemWithInvalidSellerId() {
        when(userDAO.retrieveById(anyInt())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        Response response = itemResource.post(item, auth_user);
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST_400);
        assertEquals(response.getEntity(), "Error: Seller does not exist");
    }

    @Test
    public void testExceptionOnPostingItemWithInvalidMinBidAmount() {
        when(item.getSellerId()).thenReturn((int)Math.random());
        when(item.getMinBidAmount()).thenReturn(0);
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        Response response = itemResource.post(item, auth_user);
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST_400);
        assertEquals(response.getEntity(), "Error: Minimum bid amount cannot be less than $1");
    }

    @Test
    public void testExceptionOnPostingItemWithInvalidAuctionEndTime() {
        String dummyStartTime = "1990-01-01 11:11:11";
        String dummyEndTime = "2016-01-01 11:11:11";
        int numberMoreThanZero = (int)Math.random() + 1 ;
        when(item.getSellerId()).thenReturn((int)Math.random());
        when(item.getMinBidAmount()).thenReturn(numberMoreThanZero);
        when(item.getAuctionStartTime()).thenReturn(Timestamp.valueOf(dummyStartTime));
        when(item.getAuctionEndTime()).thenReturn(Timestamp.valueOf(dummyEndTime));
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        when(itemDAO.create(any(Item.class))).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.post(item, auth_user);
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST_400);
        assertEquals(response.getEntity(), "Error: Invalid auction end time");
    }

//    @Test
//    public void testFetchingItemsBySellerId() {
//        List<Item> mockResult = new LinkedList<>();
//        mockResult.add(item);
//        when(itemDAO.findItemsBySellerId(anyInt())).thenReturn(mockResult);
//        ItemResource itemResource = new ItemResource(itemDAO);
//
//        Response response = itemResource.get((int) Math.random());
//        assertEquals(response.getEntity(), mockResult);
//    }

    @Test
    public void testFetchingActiveItems() {
        List<Item> mockResult = new LinkedList<>();
        mockResult.add(item);
        when(itemDAO.findAllActiveItems()).thenReturn(mockResult);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.get();
        assertEquals(response.getEntity(), mockResult);
    }

    @Test
    public void testFetchingItemById() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.getById((int) Math.random());
        assertEquals(response.getEntity(), item);
    }

    @Test
    public void testDeletingItemWithValidId() {
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete((int) Math.random(), auth_user);
        assertEquals(response.getStatus(), HttpStatus.NO_CONTENT_204);
    }

    @Test
    public void testDeletingItemWithInvalidId() {
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete((int) Math.random(), auth_user);
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST_400);
        assertEquals(response.getEntity(), "Error: Item not found");
    }

    @Test
    public void testDeletingItemWithInvalidCredentials() {
        when(userDAO.retrieveById(anyInt())).thenReturn(Mockito.mock(User.class));
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete((int) Math.random(), auth_user);
        assertEquals(response.getStatus(), HttpStatus.UNAUTHORIZED_401);
        assertEquals(response.getEntity(), "Error: Invalid credentials");
    }
}
