package edu.neu.cs5500.wizards.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.ItemDAO;
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

@Path("/user")
@Api(value = "/user", description = "Operations involving users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDao;
    private final ItemDAO itemDao;

    public UserResource(UserDAO userDao, ItemDAO itemDao) {
        this.userDao = userDao;
        this.itemDao = itemDao;
    }

    /**
     * Given a user, if the user is valid, creates the given user in the database. Send an error response if: (1) the
     * given user is null; (2) the username already exists in the database.
     *
     * @param user the user to create in the database
     * @return a response indicating success or failure of creating this user
     */
    @POST
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Creates a user in the database given the user", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Given user is empty"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Username is already taken!")})
    public Response create(@ApiParam(value = "User object to be created", required = true) @Valid User user) {
        if (user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Given user is empty")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        } else if (this.userDao.retrieve(user.getUsername()) != null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Username is already taken!")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        User createdUser = this.userDao.create(user);

        return Response.ok(createdUser).build();
    }


    /**
     * Updates a given user in the database. The user must be the same as the account they are trying to update. An
     * error response will be sent in the following cases: (1) The passed in user is null; (2) if the passed in
     * username does not correspond to an existing user; (3) if the user is not the same person as the user they are
     * trying to update; (4) if the user is attempting to change their username; (5) if the user's new password is less
     * than 3 characters. Else, the user will be updated in the database with all non-null fields included in the
     * given user.
     *
     * @param username  the username of the user to update
     * @param user      the user object containing new information to replace the old
     * @param auth_user the user attempting to update the database
     * @return a response code indicating success or failure of the update and containing a meaningful message
     */
    @PUT
    @Timed
    @UnitOfWork
    @Path("/{username}")
    @ExceptionMetered
    @ApiOperation(value = "Updates a certain user with new information given the user with updated information",
            notes = "Must provide the username of the user and a user object containing new information",
            response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Given user is empty"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Invalid username, update failed"),
            @ApiResponse(code = HttpStatus.UNAUTHORIZED_401, message = "Error: Invalid credentials"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Username cannot be changed"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: New password must be at least 3 characters")
    })
    public Response update(@ApiParam(value = "Username of the user to be updated", required = true) @PathParam("username") String username,
                           @ApiParam(value = "Updated user object", required = true) User user,
                           @ApiParam(hidden = true) @Auth User auth_user) {
        if (user == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Given user is empty")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        User existingUser = this.userDao.retrieve(username);
        if (existingUser == null) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Invalid username, update failed")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!auth_user.equals(existingUser)) {
            return Response
                    .status(HttpStatus.UNAUTHORIZED_401)
                    .entity("Error: Invalid credentials")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (user.getUsername() != null && !existingUser.getUsername().equals(user.getUsername())) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: Username cannot be changed")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (user.getPassword() != null && user.getPassword().length() < 3) {
            return Response
                    .status(HttpStatus.BAD_REQUEST_400)
                    .entity("Error: New password must be at least 3 characters")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
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
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        this.userDao.update(existingUser.getUsername(), existingUser.getPassword(), existingUser.getFirstName(), existingUser.getLastName(), existingUser.getAddress(), existingUser.getEmail());

        return Response.ok(existingUser).build();
    }

    /**
     * Given a username, return the user in the database with the matching username. If the user does not exist,
     * return a response error.
     *
     * @param username the username of the user to retrieve
     * @return a response code indicating failure or success and the desired user
     */
    @GET
    @Path("/{username}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Finds and returns a user from the database by username", response = User.class)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: User not found")
    })
    public Response getOne(@ApiParam(value = "Username of the user", required = true) @PathParam("username") String username) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        user.setEmail(null);
        user.setPassword(null);
        return Response.ok(user).build();
    }


    /**
     * Given a username, return all items linked to that user. If the username does not correspond with an existing
     * user, send an error response.
     *
     * @param username the username of the user which owns the items
     * @return a response indicating success or failure and if success, all the items for that user
     */
    @GET
    @Path("/{username}/items")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Finds all items linked to the specified user by username",
            response = Item.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: User not found")
    })
    public Response getItems(@ApiParam(value = "Username of the user", required = true) @PathParam("username") String username) {
        User user = this.userDao.retrieve(username);
        if (user == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        List<Item> items = this.itemDao.findItemsBySellerId(user.getId());

        //fields to ignore in the json response
        for (Item item : items) {
            if (item != null) {
                item.setSellerId(null);
                if (item.getBuyerId() == 0) { // not sold
                    item.setBuyerId(null);
                }
            }
        }

        return Response.ok(items).build();
    }

    /**
     * Given a username, find the user in the database corresponding to that username and delete the user altogether.
     * Send an error response if: (1) the username does not correspond to an existing user; (2) If the user making this
     * request and the user in the database are not the same; (3) if the user is attempting to delete an admin.
     *
     * @param username  the username corresponding to the user to delete
     * @param auth_user the user making this request
     * @return a response indicating success or failure
     */
    @DELETE
    @Path("/{username}")
    @Timed
    @UnitOfWork
    @ExceptionMetered
    @ApiOperation(value = "Deletes a specified user from the database by username")
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "Error: User not found"),
            @ApiResponse(code = HttpStatus.UNAUTHORIZED_401, message = "Error: Invalid credentials"),
            @ApiResponse(code = HttpStatus.BAD_REQUEST_400, message = "Error: Admin cannot be deleted"),
    })
    public Response delete(@ApiParam(value = "Username of the user to be deleted", required = true) @PathParam("username") String username,
                           @ApiParam(hidden = true) @Auth User auth_user) {
        User existingUser = this.userDao.retrieve(username);
        if (existingUser == null) {
            return Response
                    .status(HttpStatus.NOT_FOUND_404)
                    .entity("Error: User not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!auth_user.equals(existingUser)) {
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
        return Response.status(HttpStatus.NO_CONTENT_204).build();
    }
}
