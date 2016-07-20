package edu.neu.cs5500.wizards.resources;

import edu.neu.cs5500.wizards.core.Feedback;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.FeedbackDAO;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by amala on 18/07/16.
 */
public class FeedbackResourceTest {

    @Mock
    UserDAO userDAO;
    FeedbackDAO feedbackDAO;

    @Mock
    User user;

    @Mock
    Feedback feedback;

    Random rand = new Random();

    @Before
    public void before() {
        userDAO = Mockito.mock(UserDAO.class);
        feedbackDAO =Mockito.mock(FeedbackDAO.class);

        user = Mockito.mock(User.class);
        feedback = Mockito.mock(Feedback.class);

        //when(auth_user.getUsername()).thenReturn(RandomStringUtils.random(5));

        when(user.getUsername()).thenReturn(RandomStringUtils.random(5));
        when(userDAO.retrieveById(anyInt())).thenReturn(user);
        when(userDAO.retrieve(anyString())).thenReturn(user);
    }

    @Test
    public void testCreateFeedback(){
        when(userDAO.create(any(User.class))).thenReturn(user);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.create(user.getUsername(),feedback);
        assertEquals(HttpStatus.OK_200, response.getStatus());
       // assertEquals(feedback, response.getEntity());

    }

    @Test
    public void testCreateFeedbackWithInvalidUser(){
        String randomUser = RandomStringUtils.random(10);
        when(userDAO.retrieve(anyString())).thenReturn(null);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.create(randomUser,feedback);
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User does not exist", response.getEntity());

    }

    @Test
    public void testGetAllFeedbackForUser(){
        List<Feedback> mockResult = new LinkedList<>();
        mockResult.add(feedback);
        when(feedbackDAO.retrieve(anyInt())).thenReturn(mockResult);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.getAll(RandomStringUtils.random(5));
        assertEquals(mockResult, response.getEntity());

    }

    @Test
    public void testGetAllFeedbackForInvalidUser(){
        when(userDAO.retrieve(anyString())).thenReturn(null);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.getAll(RandomStringUtils.random(10));
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User not found", response.getEntity());

    }


    @Test
    public void testGetFeedbackById(){
        when(userDAO.retrieve(anyString())).thenReturn(user);
        when(feedbackDAO.retrieveOne(anyInt())).thenReturn(feedback);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.getOne(RandomStringUtils.random(5),rand.nextInt());
        assertEquals(HttpStatus.OK_200, response.getStatus());
    }

    @Test
    public void testExceptionFetchingFeedbackForAnotherUser(){
        when(userDAO.retrieve(anyString())).thenReturn(user);
        when(feedbackDAO.retrieveOne(anyInt())).thenReturn(feedback);
        when(feedback.getUserId()).thenReturn(rand.nextInt());
        when(user.getId()).thenReturn(rand.nextInt());
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.getOne(RandomStringUtils.random(5),rand.nextInt());
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: The feedback requested does not belong to this user", response.getEntity());
    }

    @Test
    public void testExceptionFetchingInvalidFeedback(){
        when(userDAO.retrieve(anyString())).thenReturn(user);
        when(feedbackDAO.retrieveOne(anyInt())).thenReturn(null);

        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.getOne(RandomStringUtils.random(5),rand.nextInt());
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: No feedback matches your request", response.getEntity());
    }

    @Test
    public void testFetchingFeedbackForInvalidUser(){

        when(userDAO.retrieve(anyString())).thenReturn(user);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.getOne(RandomStringUtils.random(5),rand.nextInt());
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User not found", response.getEntity());

    }

    @Test
    public void testDeletingFeedbackForInvalidUser(){
        when(userDAO.retrieve(anyString())).thenReturn(null);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.delete(RandomStringUtils.random(5),rand.nextInt());
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: User does not exist", response.getEntity());

    }


    @Test
    public void testDeletingInvalidFeedback(){
        when(userDAO.retrieve(anyString())).thenReturn(user);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.delete(RandomStringUtils.random(5),rand.nextInt());
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: Feedback not found", response.getEntity());

    }

    @Test
    public void testExceptionDeletingFeedbackForAnotherUser(){
        when(userDAO.retrieve(anyString())).thenReturn(user);
        when(feedbackDAO.retrieveOne(anyInt())).thenReturn(feedback);
        when(feedback.getUserId()).thenReturn(rand.nextInt());
        when(user.getId()).thenReturn(rand.nextInt());
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.delete(RandomStringUtils.random(5),rand.nextInt());
        assertEquals(HttpStatus.BAD_REQUEST_400, response.getStatus());
        assertEquals("Error: The feedback requested does not belong to this user", response.getEntity());
    }

    @Test
    public void testDeletingFeedbackById(){
        when(userDAO.retrieve(anyString())).thenReturn(user);
        when(feedbackDAO.retrieveOne(anyInt())).thenReturn(feedback);
        FeedbackResource feedbackResource = new FeedbackResource(feedbackDAO,userDAO);

        Response response = feedbackResource.delete(RandomStringUtils.random(5),rand.nextInt());
        assertEquals(HttpStatus.NO_CONTENT_204, response.getStatus());
    }

}
