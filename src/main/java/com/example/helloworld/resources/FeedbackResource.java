package com.example.helloworld.resources;

import com.example.helloworld.db.FeedbackDAO;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by susannaedens on 6/23/16.
 */
@Path("/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    private final FeedbackDAO feedbackDao;

    public FeedbackResource(FeedbackDAO feedbackDao) {this.feedbackDao = feedbackDao;}



}
