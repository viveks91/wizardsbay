package com.example.helloworld.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.core.Bid;
import com.example.helloworld.core.Feedback;
import com.example.helloworld.db.FeedbackDAO;
import com.example.helloworld.exception.ResponseException;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

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

    //Create feedback
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Feedback post(Feedback feedback) {

        if (feedback == null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Invalid Feedback");
        }

        Feedback newfeedback = feedbackDao.create(feedback.getUserId(), feedback.getDesc());
        return newfeedback;
    }

    //get all feedback for a given user
    @GET
    @Path("/user/{userId}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Set<Feedback> get(@PathParam("userId") int userId) {
        return feedbackDao.retrieve(userId);
    }


}
