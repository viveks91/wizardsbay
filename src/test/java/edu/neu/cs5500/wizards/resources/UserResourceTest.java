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
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class UserResourceTest {

    @Mock
    UserDAO userDAO;

    @Mock
    ItemDAO itemDAO;

    @Mock
    User user;

    @Mock
    User testUser;

    @Mock
    User auth_user;

    @Mock
    Item item;

    // This function gets invoked before each of the tests below
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

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
    public void testCreatingUserWhenContentIsNull() {
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.create(null);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Given user is empty", response.getEntity());
    }

    @Test
    public void testCreatingWithExistingUsername() {
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.create(user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Username is already taken!", response.getEntity());
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
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
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
    public void testFetchingItemsWhenInvalidUser() {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.getItems(RandomStringUtils.random(5));
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
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
        assertEquals(HttpStatus.NOT_FOUND_404, response.getStatus());
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
        when(userDAO.retrieve(anyString())).thenReturn(auth_user);
        UserResource userResource = new UserResource(userDAO, itemDAO);
        String adminUsername = "admin";

        Response response = userResource.delete(adminUsername, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Admin cannot be deleted", response.getEntity());
    }

    @Test
    public void testUpdateWithInvalidUsername() {
        when(userDAO.retrieve(anyString())).thenReturn(null);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.update(RandomStringUtils.random(8), user, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Invalid username, update failed", response.getEntity());
    }

    @Test
    public void testUpdateWithInvalidUserDetails() {

        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.update(RandomStringUtils.random(8), null, auth_user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Given user is empty", response.getEntity());
    }

    @Test
    public void testUpdateWithInvalidCredentials() {
        when(userDAO.retrieve(anyString())).thenReturn(Mockito.mock(User.class));
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.update(RandomStringUtils.random(8), user, auth_user);
        assertEquals(HttpStatus.UNAUTHORIZED_401, response.getStatus());
        assertEquals("Error: Invalid credentials", response.getEntity());
    }

    @Test
    public void testUpdateWithChangeInUsername() {
        when(user.getUsername()).thenReturn(RandomStringUtils.random(5))
                                .thenReturn(RandomStringUtils.random(5))
                                .thenReturn(RandomStringUtils.random(5));
        when(userDAO.retrieve(anyString())).thenReturn(user);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.update(RandomStringUtils.random(5), user, user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Username cannot be changed", response.getEntity());
    }

    @Test
    public void testUpdateInvalidShortPassword() {
        String randomString = RandomStringUtils.random(5);
        when(user.getUsername()).thenReturn(randomString);
        when(user.getPassword()).thenReturn(RandomStringUtils.random(2));
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.update(randomString, user, user);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: New password must be at least 3 characters", response.getEntity());
    }

    @Test
    public void testSuccessfulUpdate() {
        String randomString = RandomStringUtils.random(5);
        when(user.getUsername()).thenReturn(randomString);
        UserResource userResource = new UserResource(userDAO, itemDAO);

        Response response = userResource.update(randomString, user, user);
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }
}
