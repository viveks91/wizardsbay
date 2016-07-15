package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Feedback;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.FeedbackDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by susannaedens on 6/23/16.
 */
@Path("/feedback")
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
    public Response post(Feedback feedback) {
        User user = this.userDao.retrieveById(feedback.getUserId());
        if(user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User does not exist")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Feedback newFeedback = this.feedbackDao.create(feedback.getUserId(), feedback.getRating(), feedback.getFeedbackDescription());
        return Response.ok(newFeedback).build();
    }


//    /**
//     * Given an id for a user, retrieve the list of feedback for that user. If the user does not exist, no feedback
//     * will be returned.
//     *
//     * @param userid the id of the user
//     * @return a list of feedback for a given user
//     */
//    @GET
//    @Path("/user/{userid}")
//    @Timed
//    @UnitOfWork
//    @ExceptionMetered
//    public Response get(@PathParam("userid") int userid) {
//        return Response.ok(feedbackDao.retrieve(userid)).build();
//    }

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
    public Response getOne(@PathParam("id") int id) {
        Feedback feedback = this.feedbackDao.retrieveOne(id);
        if (feedback == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: No feedback matches your request")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        return Response.ok(feedback).build();
    }

    /**
     * Given a feedback, delete that feedback from the database. If the feedback is not found, throw an exception.
     * If the feedback is successfully deleted, return a 204 response code.
     *
     * @param existingFeedback the feedback to delete
     * @return a 204 response code representing successful deletion
     */
    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response delete(Feedback existingFeedback) {
        Feedback feedback = this.feedbackDao.retrieveOne(existingFeedback.getId());
        if (feedback == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Feedback not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        this.feedbackDao.delete(feedback);
        return Response.status(HttpStatus.NO_CONTENT_204).build();
    }
}
