package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Feedback;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.FeedbackDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.*;
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
@Api(value = "feedback", description = "Operations involving feedback for a specific user")
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
     * Given a username and a valid feedback, create the feedback in the database for that user. If the user does not
     * exist, send a response error.
     *
     * @param feedback the feedback to create in the database
     * @return a response indicating the success or failure of creating this feedback
     */
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Creates a feedback in the database for specified user given the feedback",
            response = Feedback.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: The user you are trying to create feedback for does not exist")
    })
    public Response create(@ApiParam(value = "Username of the user getting the feedback", required = true) @PathParam("username") String username,
                           @ApiParam(value = "Feedback to be posted", required = true) @Valid Feedback feedback) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: The user you are trying to create feedback for does not exist")
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
    @ApiOperation(value = "Finds all feedback for a user by username",
            response = Feedback.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: User not found")
    })
    public Response getAll(@ApiParam(value = "Username of the user", required = true) @PathParam("username") String username) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        List<Feedback> feedbacks = this.feedbackDao.retrieve(user.getId());

        // field to ignore in response
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
    @ApiOperation(value = "Finds feedback by it's id", response = Feedback.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: User not found"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: No feedback matches your request"),
            @ApiResponse(code = HttpStatus.FORBIDDEN_403, message = "Forbidden: The feedback requested does not belong to this user")
    })
    public Response getOne(@ApiParam(value = "Username of the user", required = true) @PathParam("username") String username,
                           @ApiParam(value = "Feedback Id", required = true) @PathParam("id") int id) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Feedback feedback = this.feedbackDao.retrieveOne(id);
        if (feedback == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: No feedback matches your request")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!feedback.getUserId().equals(user.getId())) {
            return Response
                    .status(HttpStatus.FORBIDDEN_403)
                    .entity("Forbidden: The feedback requested does not belong to this user")
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
    @ApiOperation(value = "Deletes a feedback from the database by id")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: User does not exist"),
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: Feedback not found"),
            @ApiResponse(code = HttpStatus.FORBIDDEN_403, message = "Forbidden: The feedback requested does not belong to this user"),
            @ApiResponse(code = HttpStatus.UNAUTHORIZED_401, message = "Error: Invalid credentials")
    })
    public Response delete(@ApiParam(value = "Username of the user", required = true) @PathParam("username") String username,
                           @ApiParam(value = "Id of the feedback to be deleted", required = true) @PathParam("feedbackId") int feedbackId,
                           @ApiParam(hidden = true) @Auth User auth_user) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: User does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Feedback feedback = this.feedbackDao.retrieveOne(feedbackId);
        if (feedback == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: Feedback not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!feedback.getUserId().equals(user.getId())) {
            return Response
                    .status(HttpStatus.FORBIDDEN_403)
                    .entity("Forbidden: The feedback requested does not belong to this user")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!auth_user.equals(user)) {
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        this.feedbackDao.delete(feedback);
        return Response.status(HttpStatus.NO_CONTENT_204).build();
    }
}
