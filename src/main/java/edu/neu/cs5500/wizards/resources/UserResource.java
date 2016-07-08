package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.UserDAO;
import edu.neu.cs5500.wizards.exception.ResponseException;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

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

    //Create user
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public User post(User user) {
        if (user == null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "User is empty");
        }
        else if (userDao.retrieve(user.getUsername()) != null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "User already exists!");
        }
        User createdUser = userDao.create(user);
        return createdUser;
    }


    //Update an existing user
    @PUT
    @Timed
    @UnitOfWork
    @Path("/{username}")
    @ExceptionMetered
    public User put(@PathParam("username") String username, User user) {
        User existingUser = userDao.retrieve(username);
        if(existingUser == null){
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "Invalid username, update failed");
        }
        if (user.getPassword() != null) {
            String password = user.getPassword();
            existingUser.setPassword(password);
        }
        if (user.getFirstname() != null) {
            existingUser.setFirstname(user.getFirstname());
        }
        if (user.getLastname() != null) {
            existingUser.setLastname(user.getLastname());
        }
        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
        }
        if (user.getUsername() != null) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "You cannot update a username");
        }
        userDao.update(existingUser.getUsername(), existingUser.getPassword(), existingUser.getFirstname(), existingUser.getLastname(), existingUser.getAddress());

        return existingUser;
    }
	
    @GET
    @Path("/{username}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    //Get user by username
    public User get(@PathParam("username") String username, @Auth User auth_user) {
        System.out.println(auth_user);
        User user = userDao.retrieve(username);
        return user;
    }

    @DELETE
    @Timed
    @UnitOfWork
    @ExceptionMetered
    /* delete user by username*/
    public String delete(User existingUser) {
        if(existingUser == null || userDao.retrieve(existingUser.getUsername()) == null){
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "No such user exist");
        }
        if (existingUser.getUsername().equals("admin")) {
            ResponseException.formatAndThrow(Response.Status.BAD_REQUEST, "You cannot delete the admin user");
        }
        userDao.delete(existingUser);

        return "{\"status\": 204}";
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }
}
