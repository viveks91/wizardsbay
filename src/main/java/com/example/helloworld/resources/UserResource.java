package com.example.helloworld.resources;

import io.dropwizard.hibernate.UnitOfWork;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.exception.ResponseException;
import com.example.helloworld.core.User;
import com.example.helloworld.db.UserDAO;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDao;
    

    public UserResource(UserDAO userDao) {
        this.userDao = userDao;
    }

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User post(User user) {
        User existingUser = userDao.retrieve(user.getUsername());
        if (existingUser == null) {
            userDao.create(user.getUsername(), user.getPassword(), user.getFirstname(), user.getLastname() , user.getAddress());
            existingUser = userDao.retrieve(user.getUsername());
        }
        else {
            String sentPassword = user.getPassword();
            if (!sentPassword.equals(existingUser.getPassword())) {
                ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Invalid email / password");
            }
            user = existingUser;
        }

        //Session session = sessionDao.createSession(user.getEmail());
        //ObjectNode object = (ObjectNode)Jackson.newObjectMapper().convertValue(user, JsonNode.class);
        //object.put("session", session.getSession());
        return existingUser;
    }

//    @PUT
//    @Timed
//    @UnitOfWork
//    @ExceptionMetered
//    public User put(User existingUser, User user) {
//        if (user.getPassword() != null) {
//            String password = user.getPassword();
//            existingUser.setPassword(password);
//        }
//        if (user.getFirstname() != null) {
//            existingUser.setFirstname(user.getFirstname());
//        }
//        if (user.getLastname() != null) {
//            existingUser.setLastname(user.getLastname());
//        }
//        if (user.getAddress() != null) {
//            existingUser.setAddress(user.getAddress());
//        }
//        if (user.getUsername() != null) {
//            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "You cannot update a username");
//        }
//        userDao.update(existingUser.getFirstname(), existingUser.getLastname(), existingUser.getAddress());
//
//        return existingUser;
//    }

    @GET
    @Path("/{username}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User get(@PathParam("username") String username) {
        User user = userDao.retrieve(username);
        System.out.println(user);
        return user;
    }

    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public String delete(User existingUser) {
        if (existingUser.getUsername().equals("admin")) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "You cannot delete the admin user");
        }
        userDao.delete(existingUser);
        return "{}";
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }
}