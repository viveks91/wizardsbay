package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Feedback;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.FeedbackDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by susannaedens on 6/23/16.
 */
@Path("/user/{username}/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    private final FeedbackDAO feedbackDao;
    private final UserDAO userDao;

    public FeedbackResource(FeedbackDAO feedbackDao, UserDAO userDao) {
        this.feedbackDao = feedbackDao;
        this.userDao = userDao;
    }

    /**
     * Creates a feedback for a user.
     *
     * @param feedback the feedback to create
     * @return the created feedback
     */
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response post(@PathParam("username") String username, @Valid Feedback feedback) {
        User user = this.userDao.retrieve(username);
        if(user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Feedback newFeedback = this.feedbackDao.create(user.getId(), feedback.getRating(), feedback.getFeedbackDescription());
        return Response.ok(newFeedback).build();
    }

    /**
     * Given a username, retrieve the list of feedback for that user. If the user does not exist, no feedback
     * will be returned.
     *
     * @param username of a user
     * @return a list of feedback for a given user
     */
    @GET
    @Path("/all")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response getFeedback(@PathParam("username") String username) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Feedback> feedbacks = this.feedbackDao.retrieve(user.getId());

        // ignore fields
        for (Feedback feedback : feedbacks) {
            feedback.setTime(null);
        }

        return Response.ok(feedbacks).build();
    }

    /**
     * Given an id, retrieve feedback with the matching id. If there is no such feedback in the database, throw
     * an exception.
     *
     * @param id the id of the feedback
     * @return the feedback matching the given id
     */
    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response getOne(@PathParam("username") String username, @PathParam("id") int id) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Feedback feedback = this.feedbackDao.retrieveOne(id);
        if (feedback == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: No feedback matches your request")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!feedback.getUserId().equals(user.getId())) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: The feedback requested does not belong to this user")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        feedback.setUsername(username);
        return Response.ok(feedback).build();
    }

    /**
     * Given a feedback, delete that feedback from the database. If the feedback is not found, throw an exception.
     * If the feedback is successfully deleted, return a 204 response code.
     *
     * @param feedbackId the feedbackId to delete
     * @return a 204 response code representing successful deletion
     */
    @DELETE
    @Path("/{feedbackId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response delete(@PathParam("username") String username, @PathParam("feedbackId") int feedbackId) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Feedback feedback = this.feedbackDao.retrieveOne(feedbackId);
        if (feedback == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Feedback not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!feedback.getUserId().equals(user.getId())) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: The feedback requested does not belong to this user")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        this.feedbackDao.delete(feedback);
        return Response.status(HttpStatus.NO_CONTENT_204).build();
    }
}
