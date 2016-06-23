package com.example.helloworld.db;

import com.example.helloworld.core.Feedback;
import com.example.helloworld.core.User;
import com.example.helloworld.mapper.FeedbackMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Set;

/**
 * Created by susannaedens on 6/20/16.
 */

@RegisterMapper(FeedbackMapper.class)
public interface FeedbackDAO {

    @SqlUpdate("insert into feedback (userId, desc) values (:userId, :desc)")
    public Feedback create(@Bind("userId") int userId, @Bind("desc") String desc);

    @SqlQuery("select desc from feedback where userId = :userId")
    public Set<Feedback> retrieve(@Bind("userId") int userId);

    //> Delete feedback by unique id
    @SqlUpdate("delete from feedback where id = :id")
    public void delete(@BindBean Feedback feedback);

    //> purpose of this method?
    @SqlQuery("select id, userId, desc from feedback limit :limit offset :offset")
    public Set<User> list(@Bind("limit") int limit, @Bind("offset") int offset);

}
