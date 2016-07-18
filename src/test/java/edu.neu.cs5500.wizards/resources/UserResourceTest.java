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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class UserResourceTest {

    @Mock
    UserDAO userDAO;
    ItemDAO itemDAO;

    @Mock
    User user;

    @Mock
    User testUser;

    @Mock
    User auth_user;

    @Mock
    Item item;


    Random rand = new Random();

    // This function gets invoked before each of the tests below
    @Before
    public void before() {
        userDAO = Mockito.mock(UserDAO.class);
        itemDAO = Mockito.mock(ItemDAO.class);
        user = Mockito.mock(User.class);
        testUser = Mockito.mock(User.class);
        auth_user = Mockito.mock(User.class);

        when(auth_user.getUsername()).thenReturn(RandomStringUtils.random(5));

        when(user.getUsername()).thenReturn(RandomStringUtils.random(5));
        when(userDAO.retrieveById(anyInt())).thenReturn(user);
        when(userDAO.retrieve(anyString())).thenReturn(user);
    }

    @Test
    public void testCreateUser() {
        when(userDAO.create(any(User.class))).thenReturn(testUser);
        when(userDAO.retrieve(anyString())).thenReturn(null);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.create(testUser);
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(testUser, response.getEntity());
    }

    @Test
    public void testExceptionOnPostingUserWhenContentIsNull() {
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.create(null);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User is empty", response.getEntity());
    }

    @Test
    public void testExceptionOnCreatingExistingUser() {
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.create(user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User already exists!", response.getEntity());
    }

    @Test
    public void testGetUserByUsername() {
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.getOne(RandomStringUtils.random(10));
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void testGetUserWhenUsernameIsNull() {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.getOne(RandomStringUtils.random(5));
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User not found", response.getEntity());
    }

    @Test
    public void testFetchingItemsByUsername() {
        List<Item> mockResult = new LinkedList<>();
        mockResult.add(item);
        when(itemDAO.findItemsBySellerId(anyInt())).thenReturn(mockResult);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.getItems(RandomStringUtils.random(5));
        assertEquals(mockResult, response.getEntity());
    }

    @Test
    public void testFetchingItemsWhenInvaliduser() {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.getItems(RandomStringUtils.random(5));
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User not found", response.getEntity());
    }

    @Test
    public void testDeletingUserWithValidUsername() {
        when(user.getUsername()).thenReturn(RandomStringUtils.random(8));
        when(userDAO.retrieve(anyString())).thenReturn(user);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.delete(RandomStringUtils.random(8), user);
        assertEquals(HttpStatus.NO_CONTENT_204, response.getStatus());
    }


    @Test
    public void testDeletingNonExistingUser() {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.delete(RandomStringUtils.random(8), auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User not found", response.getEntity());
    }

    @Test
    public void testDeletingUserWithInvalidCredentials() {
        when(userDAO.retrieve(anyString())).thenReturn(Mockito.mock(User.class));
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.delete(RandomStringUtils.random(8), auth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

    @Test
    public void testDeletingUserWithAdmin() {
        when(userDAO.retrieve(anyString())).thenReturn(user);
        UserResource userResource = new UserResource(userDAO, itemDAO);
        User testUser = new User();
        testUser.setUsername("admin");
        Response response = userResource.delete(RandomStringUtils.random(8), auth_user);
    }
}
