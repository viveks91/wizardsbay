package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import edu.neu.cs5500.wizards.mail.MailService;
import edu.neu.cs5500.wizards.scheduler.SchedulingAssistant;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ItemResource.class, LoggerFactory.class})
public class ItemResourceTest {

    @Mock
    ItemDAO itemDAO;

    @Mock
    UserDAO userDAO;

    @Mock
    User auth_user;

    @Mock
    Item item;

    @Mock
    Item item2;

    @Mock
    MailService mailService;

    @Mock
    SchedulingAssistant schedulingAssistant;

    @Mock
    Logger logger;

    Random rand = new Random();
    Timestamp THE_FUTURE;

    // This function gets invoked before each of the tests below
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(LoggerFactory.class);

        when(auth_user.getUsername()).thenReturn(RandomStringUtils.random(5));
        when(auth_user.getEmail()).thenReturn(RandomStringUtils.random(5));
        when(userDAO.retrieveById(anyInt())).thenReturn(auth_user);
        when(userDAO.retrieve(anyString())).thenReturn(auth_user);

        PowerMockito.whenNew(MailService.class).withAnyArguments().thenReturn(mailService);
        PowerMockito.whenNew(SchedulingAssistant.class).withAnyArguments().thenReturn(schedulingAssistant);
        PowerMockito.when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);

        THE_FUTURE = new Timestamp(System.currentTimeMillis() + 1000000L);
    }

    @Test
    public void testItemCreation() {
        String dummyStartTime = "1990-01-01 11:11:11";
        String dummyEndTime = "2100-01-01 11:11:11";
        int numberMoreThanZero = (int) Math.random() + 1;
        when(item.getSellerId()).thenReturn((int) Math.random());
        when(item.getMinBidAmount()).thenReturn(numberMoreThanZero);
        when(item.getAuctionStartTime()).thenReturn(Timestamp.valueOf(dummyStartTime));
        when(item.getAuctionEndTime()).thenReturn(Timestamp.valueOf(dummyEndTime));
        when(itemDAO.create(any(Item.class))).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.create(item, auth_user);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(item, response.getEntity());
    }

    @Test
    public void testPostingItemWhenContentIsNull() {
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.create(null, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Invalid item", response.getEntity());
    }

    @Test
    public void testPostingItemWithInvalidCredentials() {
        when(userDAO.retrieve(anyString())).thenReturn(Mockito.mock(User.class));
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.create(item, auth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

    @Test
    public void testPostingItemWithInvalidSellerId() {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.create(item, auth_user);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Seller does not exist", response.getEntity());
    }

    @Test
    public void testPostingItemWithInvalidAuctionEndTime() {
        String dummyEndTime = "2016-01-01 11:11:11";
        int numberMoreThanZero = (int) Math.random() + 1;
        when(item.getSellerId()).thenReturn(rand.nextInt());
        when(item.getMinBidAmount()).thenReturn(numberMoreThanZero);
        when(item.getAuctionEndTime()).thenReturn(Timestamp.valueOf(dummyEndTime));
        when(itemDAO.create(any(Item.class))).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.create(item, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Invalid auction end time", response.getEntity());
    }

    @Test
    public void testFetchingActiveItems() {
        List<Item> mockResult = new LinkedList<>();
        mockResult.add(item);
        when(itemDAO.findAllActiveItems()).thenReturn(mockResult);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.getActive();
        assertEquals(mockResult, response.getEntity());
    }


    @Test
    public void testGetOneInvalidItem() {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.getOne(rand.nextInt());
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Item not found", response.getEntity());
    }

    @Test
    public void testFetchingItemById() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.getOne(rand.nextInt());
        assertEquals(item, response.getEntity());
    }

    @Test
    public void testDeletingItemWithValidId() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete(rand.nextInt(), auth_user);
        assertEquals(HttpStatus.NO_CONTENT_204, response.getStatus());
        assertEquals(null, response.getEntity());
    }

    @Test
    public void testDeletingItemWithInvalidItemId() {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete(rand.nextInt(), auth_user);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Item not found", response.getEntity());
    }

    @Test
    public void testDeletingItemWithInvalidCredentials() {
        when(userDAO.retrieveById(anyInt())).thenReturn(Mockito.mock(User.class));
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.delete(rand.nextInt(), auth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

    @Test
    public void testUpdateWithInvalidItem() {
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        int randomInt = rand.nextInt();

        Response response = itemResource.update(randomInt, null, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Invalid item", response.getEntity());
    }

    @Test
    public void testUpdateWithNonExistingItem() {
        when(itemDAO.findItemById(anyInt())).thenReturn(null);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        int randomInt = rand.nextInt();

        Response response = itemResource.update(randomInt, item, auth_user);
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
        assertEquals("Error: Item not found, update failed", response.getEntity());
    }

    @Test
    public void testUpdateWithDifferentSeller() {
        when(item.getSellerUsername()).thenReturn(RandomStringUtils.random(5));
        when(item2.getSellerUsername()).thenReturn(RandomStringUtils.random(5));
        when(itemDAO.findItemById(anyInt())).thenReturn(item2);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        int randomInt = rand.nextInt();

        Response response = itemResource.update(randomInt, item, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Seller cannot be changed", response.getEntity());
    }

    @Test
    public void testUpdateWithInvalidCredentials() {
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        when(userDAO.retrieveById(anyInt())).thenReturn(Mockito.mock(User.class));
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        int randomInt = rand.nextInt();

        Response response = itemResource.update(randomInt, item, auth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

    @Test
    public void testUpdateWithDifferentItemId() {
        when(item.getId()).thenReturn(rand.nextInt());
        when(item2.getId()).thenReturn(rand.nextInt());
        when(itemDAO.findItemById(anyInt())).thenReturn(item2);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        int randomInt = rand.nextInt();

        Response response = itemResource.update(randomInt, item, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Item id cannot be changed", response.getEntity());
    }

    @Test
    public void testUpdateWithInvalidAuctionEndTime() {
        String dummyEndTime1 = "2022-01-01 11:11:11";
        String dummyEndTime2 = "2016-01-01 11:11:11";
        when(item.getAuctionEndTime()).thenReturn(Timestamp.valueOf(dummyEndTime1));
        when(item2.getAuctionEndTime()).thenReturn(Timestamp.valueOf(dummyEndTime2));
        when(itemDAO.findItemById(anyInt())).thenReturn(item2);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        int randomInt = rand.nextInt();

        Response response = itemResource.update(randomInt, item, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Auction end time cannot be changed, since it has already passed", response.getEntity());
    }

    @Test
    public void testUpdateWithInvalidBidAmount() {
        when(item.getMinBidAmount()).thenReturn(0);
        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);
        int randomInt = rand.nextInt();

        Response response = itemResource.update(randomInt, item, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Minimum bid amount cannot be less than $1", response.getEntity());
    }

    @Test
    public void testSuccessfulUpdate() {
        int randomInt = rand.nextInt();
        String randString = RandomStringUtils.random(5);
        Item randItem = new Item();
        randItem.setAuctionEndTime(THE_FUTURE);
        randItem.setItemName(randString);
        randItem.setItemDescription(randString);
        randItem.setMinBidAmount(33);
        when(item2.getAuctionEndTime()).thenReturn(THE_FUTURE);
        when(item2.getMinBidAmount()).thenReturn(32);
        when(itemDAO.findItemById(anyInt())).thenReturn(item2);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.update(randomInt, randItem, auth_user);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(item2, response.getEntity());
    }

    @Test
    public void testItemSearchByKey() {
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);
        when(itemDAO.searchItems(anyString())).thenReturn(items);
        ItemResource itemResource = new ItemResource(itemDAO, userDAO);

        Response response = itemResource.getBySearchKey(RandomStringUtils.random(5));
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(items, response.getEntity());
    }
}
