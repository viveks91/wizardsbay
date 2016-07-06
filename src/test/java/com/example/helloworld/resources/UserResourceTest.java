package com.example.helloworld.resources;

import com.example.helloworld.core.User;
import com.example.helloworld.db.UserDAO;
import com.example.helloworld.mapper.UserMapper;
import com.example.helloworld.resources.UserResource;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectReader;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;

import java.io.IOException;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.glassfish.grizzly.http.CookiesBuilder.client;
//import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.dropwizard.testing.ResourceHelpers.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;


public class UserResourceTest {
    private static final UserDAO dao = mock(UserDAO.class);

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new UserResource(dao))
            .build();

    private final User expectedUser = new User("james", "password", "James", "Potter", "James's address");

    @Before
    public void setup() {
        // Before each test, we re-instantiate our resource so it will reset
        // the resource. It is good practice when dealing with a class that
        // contains mutable data to reset it so tests can be ran independently
        // of each other.

    }

    @After
    public void tearDown() {
        // we have to reset the mock after each test because of the
        // @ClassRule
        reset(dao);
    }

    @Test
    public void UserResourceGetTest() throws IOException {
        // Hit the endpoint and get the raw json string
        when(dao.retrieve("james")).thenReturn(expectedUser);
        String response = resources.client().target("/user/james")
                .request().get(String.class);
        assertThat(response)
                .isNotNull()
                .isNotEmpty();

        // The object responsible for translating json to our class
        ObjectReader reader = resources.getObjectMapper().reader(User.class);

        // Deserialize our actual and expected responses
        User actualUser = reader.readValue(response);

        assertThat(actualUser)
                .isEqualTo(expectedUser);
    }

    @Test
    public void UserResourceNegativeGetTest() throws IOException {
        // Hit the endpoint and get the raw json string
        when(dao.retrieve("james")).thenReturn(expectedUser);
        String response = resources.client().target("/user/invalid_user")
                .request().get(String.class);
        assertThat(response)
                .isNullOrEmpty();

    }

    @Test
    public void UserResourcePostTest() throws IOException {

        when(dao.retrieve("james")).thenReturn(expectedUser);
        Response response = resources.client().target("/user")
                .request().post(Entity.json(expectedUser));

        assertThat(response)
                .isNotNull();

        User actualUser = response.readEntity(User.class);
//        System.out.println("actual user:");
//        System.out.println(actualUser);
//        System.out.println("expected user:");
//        System.out.println(expectedUser);
        assertThat(actualUser)
                .isEqualTo(expectedUser);
    }

//    @Test
//    public void UserResourceNegativePostTest() throws IOException {
//
//        final User newUser = new User("jame", null, "James", "Potter", "James's address");
//
//        when(dao.retrieve("jame")).thenReturn(newUser);
//
//        Response response = resources.client().target("/user")
//                .request().post(Entity.json(newUser));
//        System.out.println(Entity.json(newUser));
//        System.out.println(response);
//
//        assertThat(response)
//                .isNotNull();
//
//        User actualUser = response.readEntity(User.class);
////        System.out.println("actual user:");
////        System.out.println(actualUser);
////        System.out.println("expected user:");
////        System.out.println(expectedUser);
//
//
//
//////        // The expected json that will be returned
//////        String json = "{ \"username\": \"james\",\"password\": \"password\",\"firstname\": \"James\",\"lastname\": \"Potter\", \"address\": \"James's address\" }";
////
////        // The object responsible for translating json to our class
////        ObjectReader reader = resources.getObjectMapper().reader(User.class);
////
////
////        // Deserialize our actual and expected responses
////        User actualUser = reader.readValue(response);
////
//        assertThat(actualUser)
//                .isEqualTo(newUser);
////
////
////        assertThat(resources.client().target("/user/invalid_user")
////                .request().get(String.class))
////                .isNullOrEmpty();
////
//////        assertThat(actual.getContent())
//////                .isEqualTo(expected.getContent())
//////                .isEqualTo("Hello, dropwizard!");
//    }

//    @Test
//    public void UserResourceDeleteTest() throws IOException{
//
//        final User deleteUser = new User("james", "james", "James", "Potter", "James's address");
//
//        when(dao.retrieve("james")).thenReturn(deleteUser);
//
//        Response response = resources.client().target("/user")
//                .request().delete(Entity.json(deleteUser));
//
//
//
//
//    }

//        UserResourceTest() {
//            when(store.fetchPerson(anyString())).thenReturn(user);
//            addResource(new UserResource(dao));
//        }

//    @Test
//    public void simpleResourceTest() throws Exception {
//        assertThat(client().resource("/person/blah").get(User.class))
//                .isEqualTo(user);
//
//        verify(store).fetchPerson("blah");
//    }
}
