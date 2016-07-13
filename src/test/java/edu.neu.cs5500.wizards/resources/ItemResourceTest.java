package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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
    Item item;

    // This function gets invoked before each of the tests below
    @Before
    public void before() {
        itemDAO = Mockito.mock(ItemDAO.class);
        item = Mockito.mock(Item.class);
    }

    @Test
    public void testItemCreation() {
        when(itemDAO.create(any(Item.class))).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Item randomItem = new Item();
        Item response = itemResource.post(randomItem);
        assertEquals(response, item);
    }

    @Test(expected = WebApplicationException.class)
    public void testExceptionOnPostingItemWhenContentIsNull() {
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        Item response = itemResource.post(null);
    }

    @Test
    public void testFetchingItemsBySellerId() {
        List<Item> mockResult = new LinkedList<>();
        mockResult.add(item);
        when(itemDAO.findItemBySellerId(anyInt())).thenReturn(mockResult);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        List<Item> response = itemResource.get((int) Math.random());
        assertEquals(response, mockResult);
    }

    @Test
    public void testFetchingActiveItems() {
        List<Item> mockResult = new LinkedList<>();
        mockResult.add(item);
        when(itemDAO.findAllActiveItems()).thenReturn(mockResult);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        List<Item> response = itemResource.get();
        assertEquals(response, mockResult);
    }

    @Test
    public void testFetchingItemById() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Item response = itemResource.getById((int) Math.random());
        assertEquals(response, item);
    }
    
    /*
    -- We need to create a auth user to run these tests properly

    @Test
    public void testDeletingItemWithValidId() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete((int) Math.random());
        assertEquals(response.getStatus(), 204);
    }

    @Test(expected = WebApplicationException.class)
    public void testDeletingItemWithInvalidId() {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete((int) Math.random());
    }
    
    */
}
