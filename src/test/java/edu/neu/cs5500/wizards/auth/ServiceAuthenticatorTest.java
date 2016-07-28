package edu.neu.cs5500.wizards.auth;

import com.google.common.base.Optional;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.UserDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ServiceAuthenticatorTest {
    public static String TEST_USERNAME = "testUser";
    public static String TEST_PASSWORD = "testPassword";

    @Mock
    UserDAO userDAO;

    @Mock
    User user;

    // This function gets invoked before each of the tests below
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(user.getUsername()).thenReturn(ServiceAuthenticatorTest.TEST_USERNAME);
        when(user.getPassword()).thenReturn(ServiceAuthenticatorTest.TEST_PASSWORD);
        when(userDAO.retrieve(anyString())).thenReturn(user);
    }

    @Test
    public void testAuthenticateUser() throws AuthenticationException {
        ServiceAuthenticator serviceAuthenticator = new ServiceAuthenticator(userDAO);
        BasicCredentials credentials = new BasicCredentials(ServiceAuthenticatorTest.TEST_USERNAME, ServiceAuthenticatorTest.TEST_PASSWORD);

        Optional<User> response = serviceAuthenticator.authenticate(credentials);
        assertEquals(user, response.get());
    }

    @Test
    public void testAuthenticateUserBadPassword() throws AuthenticationException {
        ServiceAuthenticator serviceAuthenticator = new ServiceAuthenticator(userDAO);
        BasicCredentials credentials = new BasicCredentials(user.getUsername(), RandomStringUtils.random(7));

        Optional<User> response = serviceAuthenticator.authenticate(credentials);
        assertEquals(Optional.absent(), response);
    }

}
