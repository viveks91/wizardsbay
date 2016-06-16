package com.example.helloworld.db;

import com.example.helloworld.core.User;
import com.example.helloworld.mapper.*;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

/**
 * Created by amala on 14/06/16.
 */
import java.util.Set;

@RegisterMapper(UserMapper.class)
public interface UserDAO {

    @SqlUpdate("insert into users (username, password, firstname, lastname, address) values (:username, :password, :firstname, :lastname, :address)")
    public void create(@Bind("username") String username, @Bind("password") String password, @Bind("firstname") String firstname, @Bind("lastname") String lastname, @Bind("address") String address);

    @SqlQuery("select id, username, password, firstname, lastname, address from users where username = :username")
    public User retrieve(@Bind("username") String username);

    @SqlUpdate("update users set firstname = :firstname, lastname = :lastname, address = :address where username = :username")
    public void update( @Bind("firstname") String firstname, @Bind("lastname") String lastname, @Bind("address") String address);

    @SqlUpdate("delete from users where username = :username")
    public void delete(@BindBean User user);

    @SqlQuery("select username, password, firstname, lastname, address from users limit :limit offset :offset")
    public Set<User> list(@Bind("limit") int limit, @Bind("offset") int offset);
}


