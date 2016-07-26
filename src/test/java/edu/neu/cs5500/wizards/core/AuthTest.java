package edu.neu.cs5500.wizards.resources;

import com.google.common.base.Optional;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.UserDAO;
import edu.neu.cs5500.wizards.auth.ServiceAuthenticator;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class AuthTest {

    @Mock
    UserDAO userDAO;

    @Mock
    User user;

    // This function gets invoked before each of the tests below
    @Before
    public void before() {
        userDAO = Mockito.mock(UserDAO.class);
        user = Mockito.mock(User.class);

        when(user.getUsername()).thenReturn("testUser");
	    when(user.getPassword()).thenReturn("testPassword");
        when(userDAO.retrieve(anyString())).thenReturn(user);
    }

    @Test
    public void testAuthenticateUser() throws AuthenticationException {
	ServiceAuthenticator serviceAuthenticator = new ServiceAuthenticator(userDAO);
	BasicCredentials credentials = new BasicCredentials(user.getUsername(),
							                        user.getPassword());

        Optional<User> response = serviceAuthenticator.authenticate(credentials);
        assertEquals(user, response.get());
    }
    
    @Test
    public void testAuthenticateUserBadPassword() throws AuthenticationException {
	ServiceAuthenticator serviceAuthenticator = new ServiceAuthenticator(userDAO);
	BasicCredentials credentials = new BasicCredentials(user.getUsername(),
							                        "BAD_PASSWORD@#$@");

        Optional<User> response = serviceAuthenticator.authenticate(credentials);
        assertEquals(Optional.absent(), response);
    }

}
