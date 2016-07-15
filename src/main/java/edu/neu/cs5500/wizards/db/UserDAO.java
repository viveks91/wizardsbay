package edu.neu.cs5500.wizards.db;

import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.mapper.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(UserMapper.class)
public interface UserDAO {

    @SqlQuery("insert into users (username, password, first_name, last_name, address) values (:username, :password, :firstName, :lastName, :address) RETURNING *")
    User create(@BindBean User user);

    @SqlQuery("select * from users where username = :username")
    User retrieve(@Bind("username") String username);

    @SqlQuery("select * from users where id = :id")
    User retrieveById(@Bind("id") int id);

    @SqlUpdate("update users set password = :password, first_name = :firstName, last_name = :lastName, address = :address where username = :username")
    void update(@Bind("username") String username, @Bind("password") String password, @Bind("firstName") String firstName, @Bind("lastName") String lastName, @Bind("address") String address);

    @SqlUpdate("delete from users where username = :username")
    void delete(@Bind("username") String username);
//
//    @SqlQuery("select username, password, firstName, lastName, address from users limit :limit offset :offset")
//    Set<User> list(@Bind("limit") int limit, @Bind("offset") int offset);
}


