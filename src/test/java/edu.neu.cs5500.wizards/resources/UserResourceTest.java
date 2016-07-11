package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.UserDAO;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class UserResourceTest {

    @Mock
    UserDAO userDAO;

    @Mock
    User user;

    // This function gets invoked before each of the tests below
    @Before
    public void before() {
        userDAO = Mockito.mock(UserDAO.class);
        user = Mockito.mock(User.class);
    }

    @Test
    public void testCreateUser() {
        when(userDAO.create(any(User.class))).thenReturn(user);
        UserResource userResource = new UserResource(userDAO);

        User randomUser = new User();
        User response = userResource.post(randomUser);
        assertEquals(response, user);
    }

    @Test(expected = WebApplicationException.class)
    public void testCreateUserIsNull() {
        when(userDAO.create(any(User.class))).thenReturn(user);
        UserResource userResource = new UserResource(userDAO);

        User response = userResource.post(null);
    }

    @Test(expected = WebApplicationException.class)
    public void testCreateExistingUser() {
        when(userDAO.retrieve(anyString())).thenReturn(user);
        when(user.getUsername()).thenReturn("existingname");
        when(userDAO.create(any(User.class))).thenReturn(user);
        UserResource userResource = new UserResource(userDAO);

        User randomUser = new User();
        randomUser.setUsername("existingname");
        User response = userResource.post(randomUser);
    }

    @Test(expected = WebApplicationException.class)
    public void testExceptionOnPostingANullUser() {
        UserResource userResource = new UserResource(userDAO);
        User response = userResource.post(null);
    }

    @Test
    public void testGetUserByUsername() {
        when(userDAO.retrieve(anyString())).thenReturn(user);
        UserResource userResource = new UserResource(userDAO);

        User response = userResource.get(RandomStringUtils.random(10), user);
        assertEquals(response, user);
    }

    @Test
    public void testDeletingUserWithValidUser() {
        when(user.getUsername()).thenReturn(RandomStringUtils.random(8));
        when(userDAO.retrieve(anyString())).thenReturn(user);
        UserResource userResource = new UserResource(userDAO);
        String username = user.getUsername();

        Response response = userResource.delete(user);
        assertEquals(response.getStatus(), 204);
    }

    @Test(expected = WebApplicationException.class)
    public void testDeletingUserWithInValidUser() {
        when(user.getUsername()).thenReturn(RandomStringUtils.random(6));
        when(userDAO.retrieve(anyString())).thenReturn(user);
        UserResource userResource = new UserResource(userDAO);

        Response response = userResource.delete(null);
    }

    @Test(expected = WebApplicationException.class)
    public void testDeletingUserWithAdmin() {
        when(userDAO.retrieve(anyString())).thenReturn(user);
        UserResource userResource = new UserResource(userDAO);
        User testUser = new User();
        testUser.setUsername("admin");
        Response response = userResource.delete(testUser);
    }
}
