package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.db.ItemDAO;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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
        ItemResource itemResource = new ItemResource(itemDAO);

        Item randomItem = new Item();
        Response response = itemResource.post(randomItem);
        assertEquals(response.getEntity(), item);
    }

    @Test
    public void testExceptionOnPostingItemWhenContentIsNull() {
        ItemResource itemResource = new ItemResource(itemDAO);
        Response response = itemResource.post(null);
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST_400);
        assertEquals(response.getEntity(), "Error: Invalid item");
    }

    @Test
    public void testFetchingItemsBySellerId() {
        List<Item> mockResult = new LinkedList<>();
        mockResult.add(item);
        when(itemDAO.findItemBySellerId(anyInt())).thenReturn(mockResult);
        ItemResource itemResource = new ItemResource(itemDAO);

        Response response = itemResource.get((int) Math.random());
        assertEquals(response.getEntity(), mockResult);
    }

    @Test
    public void testFetchingActiveItems() {
        List<Item> mockResult = new LinkedList<>();
        mockResult.add(item);
        when(itemDAO.findAllActiveItems()).thenReturn(mockResult);
        ItemResource itemResource = new ItemResource(itemDAO);

        Response response = itemResource.get();
        assertEquals(response.getEntity(), mockResult);
    }

    @Test
    public void testFetchingItemById() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO);

        Response response = itemResource.getById((int) Math.random());
        assertEquals(response.getEntity(), item);
    }

    @Test
    public void testDeletingItemWithValidId() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO);

        Response response = itemResource.delete((int) Math.random());
        assertEquals(response.getStatus(), HttpStatus.NO_CONTENT_204);
    }

    @Test
    public void testDeletingItemWithInvalidId() {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO);

        Response response = itemResource.delete((int) Math.random());
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST_400);
        assertEquals(response.getEntity(), "Error: Item not found");
    }
}
