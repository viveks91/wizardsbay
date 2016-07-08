package edu.neu.cs5500.wizards.db;

import edu.neu.cs5500.wizards.core.Feedback;
import edu.neu.cs5500.wizards.mapper.FeedbackMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by susannaedens on 6/20/16.
 */

@RegisterMapper(FeedbackMapper.class)
public interface FeedbackDAO {

    /**
     * Creates a feedback given the userid of the user to leave feedback for and the description.
     *
     * @param userid       userId of the user to leave feedback for
     * @param feedbackdesc the body of the feedback, the description
     */
    @SqlUpdate("insert into feedback (userid, feedbackdesc) values (:userid, :feedbackdesc)")
    void create(@Bind("userid") int userid, @Bind("feedbackdesc") String feedbackdesc);

    /**
     * Retrieves a single feedback based on the unique id assigned to each feedback.
     *
     * @param id an individual feedback id
     * @return the feedback with the id that matches the given id
     */
    @SqlQuery("select id, userid, feedbackdesc from feedback where id = :id")
    Feedback retrieveOne(@Bind("id") int id);

    /**
     * Retrieves a list of feedback for any given user based on the given userId.
     *
     * @param userid the id of the user we want feedback for
     * @return the list of feedback left for a given user
     */
    @SqlQuery("select * from feedback where userid = :userid")
    List<Feedback> retrieve(@Bind("userid") int userid);

    /**
     * Given a feedback, delete it from the database.
     *
     * @param feedback the feedback to delete
     */
    @SqlUpdate("delete from feedback where id = :id")
    void delete(@BindBean Feedback feedback);


}
