package edu.neu.cs5500.wizards.db;

import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.mapper.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(UserMapper.class)
public interface UserDAO {

    /**
     * Given a user object, create a new user in the database. The user object should contain a username, password,
     * first and last name, address, and email.
     *
     * @param user the user to create
     * @return the created user from the database
     */
    @SqlQuery("insert into users (username, password, first_name, last_name, address, email) values (:username, :password, :firstName, :lastName, :address, :email) RETURNING *")
    User create(@BindBean User user);

    /**
     * Given a username, retrieve the corresponding user from the database.
     *
     * @param username the username of the user
     * @return the corresponding user from the database
     */
    @SqlQuery("select * from users where username = :username")
    User retrieve(@Bind("username") String username);

    /**
     * Given an id, return the corresponding user from the database.
     *
     * @param id the id of the user
     * @return the user matching the given id from the database
     */
    @SqlQuery("select * from users where id = :id")
    User retrieveById(@Bind("id") int id);

    /**
     * Given new fields for a user and the username of that user, update the old information with the new. The fields
     * available to be updated are the password, first and last name, address, and email.
     *
     * @param username  the username of the user
     * @param password  the new password
     * @param firstName the new first name
     * @param lastName  the new last name
     * @param address   the new address
     * @param email     the new email
     */
    @SqlUpdate("update users set password = :password, first_name = :firstName, last_name = :lastName, address = :address, email = :email where username = :username")
    void update(@Bind("username") String username, @Bind("password") String password, @Bind("firstName") String firstName, @Bind("lastName") String lastName, @Bind("address") String address, @Bind("email") String email);

    /**
     * Given the user's username, delete the corresponding user from the database.
     *
     * @param username the username of the user to delete
     */
    @SqlUpdate("delete from users where username = :username")
    void delete(@Bind("username") String username);

    /**
     * Given a list of user ids, return the corresponding users from the database.
     *
     * @param userIds list of user ids
     */
    @SqlQuery("select * from users where id = any(:userIds)")
    List<User> RetrieveUsersByIds(@Bind("userIds") List<Integer> userIds);

    /**
     * Returns all users who participated in a particular item's auction
     *      *
     * @return a list of users
     */
    @SqlQuery("select users.* from bids LEFT OUTER JOIN users on (bids.bidder_id = users.id) where bids.item_id = :itemId")
    List<User> getParticipantsByItemId(@Bind("itemId") int itemId);
}


