package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Feedback;
import edu.neu.cs5500.wizards.db.FeedbackDAO;
import edu.neu.cs5500.wizards.exception.ResponseException;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by susannaedens on 6/23/16.
 */
@Path("/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    private final FeedbackDAO feedbackDao;

    public FeedbackResource(FeedbackDAO feedbackDao) {
        this.feedbackDao = feedbackDao;
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
    public Feedback post(Feedback feedback) {
        feedbackDao.create(feedback.getUserid(), feedback.getFeedbackdesc());
        Feedback newfeedback = feedbackDao.retrieveOne(feedback.getId());
        return newfeedback;
    }


    /**
     * Given an id for a user, retrieve the list of feedback for that user. If the user does not exist, no feedback
     * will be returned.
     *
     * @param userid the id of the user
     * @return a list of feedback for a given user
     */
    @GET
    @Path("/user/{userid}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public List<Feedback> get(@PathParam("userid") int userid) {
        return feedbackDao.retrieve(userid);
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
    public Feedback getOne(@PathParam("id") int id) {
        Feedback feedback = feedbackDao.retrieveOne(id);
        if (feedback == null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "No feedback matches your request");
        }
        return feedback;
    }

    /**
     * Given a feedback, delete that feedback from the database. If the feedback is not found, throw an exception.
     *
     * @param existingfeedback the feedback to delete
     * @return a string representing an empty set
     */
    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public String delete(Feedback existingfeedback) {
        Feedback feedback = feedbackDao.retrieveOne(existingfeedback.getId());
        if (feedback == null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Feedback not found");
        }
        feedbackDao.delete(feedback);
        return "{}";
    }


}
