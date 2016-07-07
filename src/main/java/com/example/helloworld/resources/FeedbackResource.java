package com.example.helloworld.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.core.Feedback;
import com.example.helloworld.db.FeedbackDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by susannaedens on 6/23/16.
 */
@Path("/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Api(value = "/feedback", description = "Feedback")
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
        feedbackDao.create(feedback.getUserId(), feedback.getDesc());
        Feedback newfeedback = feedbackDao.retrieveOne(feedback.getId());
        return newfeedback;
    }

    //get all feedback for a given user
    @GET
    @Path("/user/{userid}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public List<Feedback> get(@PathParam("userid") int userid) {
        return feedbackDao.retrieve(userid);
    }

    //get feedback by id
    @GET
    @Path("/{id}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Feedback getOne(@PathParam("id") int id) {
        return feedbackDao.retrieveOne(id);
    }

    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    /* delete feedback given feedback*/
    public String delete(Feedback existingfeedback) {

        feedbackDao.delete(existingfeedback);
        //> Why return a string?
        return "{}";
    }




}
