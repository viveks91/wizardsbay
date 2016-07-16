package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.jetty.http.HttpStatus;
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
import static org.mockito.Matchers.anyString;
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

        when(auth_user.getUsername()).thenReturn(RandomStringUtils.random(5));
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        when(userDAO.retrieve(anyString())).thenReturn(auth_user);
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
        when(itemDAO.create(any(Item.class))).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.post(item, auth_user);
        assertEquals(response.getEntity(), item);
    }

    @Test
    public void testExceptionOnPostingItemWhenContentIsNull() {
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.post(null, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Invalid item", response.getEntity());
    }

    @Test
    public void testExceptionOnPostingItemWithInvalidCredentials() {
        when(userDAO.retrieve(anyString())).thenReturn(Mockito.mock(User.class));
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.post(item, auth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

    @Test
    public void testExceptionOnPostingItemWithInvalidSellerId() {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        
        Response response = itemResource.post(item, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Seller does not exist", response.getEntity());
    }

    @Test
    public void testExceptionOnPostingItemWithInvalidAuctionEndTime() {
        String dummyEndTime = "2016-01-01 11:11:11";
        int numberMoreThanZero = (int)Math.random() + 1 ;
        when(item.getSellerId()).thenReturn((int)Math.random());
        when(item.getMinBidAmount()).thenReturn(numberMoreThanZero);
        when(item.getAuctionEndTime()).thenReturn(Timestamp.valueOf(dummyEndTime));
        when(itemDAO.create(any(Item.class))).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.post(item, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Invalid auction end time", response.getEntity());
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
        assertEquals(mockResult, response.getEntity());
    }

    @Test
    public void testFetchingItemById() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.getById((int) Math.random());
        assertEquals(item, response.getEntity());
    }

    @Test
    public void testDeletingItemWithValidId() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete((int) Math.random(), auth_user);
        assertEquals(HttpStatus.NO_CONTENT_204, response.getStatus());
    }

    @Test
    public void testDeletingItemWithInvalidItemId() {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete((int) Math.random(), auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Item not found", response.getEntity());
    }

    @Test
    public void testDeletingItemWithInvalidCredentials() {
        when(userDAO.retrieveById(anyInt())).thenReturn(Mockito.mock(User.class));
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete((int) Math.random(), auth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }
}
