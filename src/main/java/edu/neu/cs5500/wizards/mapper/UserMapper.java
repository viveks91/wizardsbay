package edu.neu.cs5500.wizards.mapper;

/**
 * Created by amala on 14/06/16.
 */

import edu.neu.cs5500.wizards.core.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User> {

    public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        User user = new User();
        user.setId(r.getInt("id"));
        user.setUsername(r.getString("username"));
        user.setPassword(r.getString("password"));
        user.setFirstName(r.getString("first_name"));
        user.setLastName(r.getString("last_name"));
        user.setAddress(r.getString("address"));
        return user;
    }
}