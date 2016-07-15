package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Feedback;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.FeedbackDAO;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDao;
    private final ItemDAO itemDao;
    private final FeedbackDAO feedbackDao;


    public UserResource(UserDAO userDao, ItemDAO itemDao, FeedbackDAO feedbackDao) {
        this.userDao = userDao;
        this.itemDao = itemDao;
        this.feedbackDao = feedbackDao;
    }

    /**
     *
     * @param user
     * @return
     */

    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response post(User user) {
        if (user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User is empty")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        else if (this.userDao.retrieve(user.getUsername()) != null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User already exists!")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        User createdUser = this.userDao.create(user);

        return Response.ok(createdUser).build();
    }


    //Update an existing user
    @PUT
    @Timed
    @UnitOfWork
    @Path("/{username}")
    @ExceptionMetered
    public Response put(@PathParam("username") String username, User user, @Auth User auth_user) {
        User existingUser = this.userDao.retrieve(username);

        if(existingUser == null){
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Invalid username, update failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if(!auth_user.equals(existingUser)){
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (user.getPassword() != null) {
            String password = user.getPassword();
            existingUser.setPassword(password);
        }
        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
        }
        if (user.getUsername() != null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Username cannot be changed")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        this.userDao.update(existingUser.getUsername(), existingUser.getPassword(), existingUser.getFirstName(), existingUser.getLastName(), existingUser.getAddress());

        return Response.ok(existingUser).build();
    }
	
    @GET
    @Path("/{username}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    //Get user by username
    public Response get(@PathParam("username") String username) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        return Response.ok(user).build();
    }

    //get all items listed by a seller
    @GET
    @Path("/{username}/items")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public Response getItems(@PathParam("username") String username) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Item> items = this.itemDao.findItemsBySellerId(user.getId());

        // ignore fields
        for (Item item : items) {
            item.setSellerId(null);
            if (item.getBuyerId() == 0) { // not sold
                item.setBuyerId(null);
            }
        }

        return Response.ok(items).build();
    }

    /**
     * Given a username, retrieve the list of feedback for that user. If the user does not exist, no feedback
     * will be returned.
     *
     * @param username of a user
     * @return a list of feedback for a given user
     */
    @GET
    @Path("/{username}/feedback")
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
            feedback.setUserId(null);
            feedback.setTime(null);
        }

        return Response.ok(feedbacks).build();
    }

    @DELETE
    @Path("/{username}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    /* delete user by username*/
    public Response delete(@PathParam("username") String username, @Auth User auth_user) {
        User existingUser = this.userDao.retrieve(username);
        if(existingUser == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if(!auth_user.equals(existingUser)){
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (username.equals("admin")) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Admin cannot be deleted")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        this.userDao.delete(username);

        return Response.status(204).build();
    }

    @OPTIONS
    @Timed
    @UnitOfWork
    @ExceptionMetered
    public void options() { }
}
