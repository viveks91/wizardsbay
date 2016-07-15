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
     * Creates a feedback given the userId of the user to leave feedback for and the description.
     *
     * @param userId       userId of the user to leave feedback for
     * @param rating       rating for the feedback
     * @param feedbackDescription the body of the feedback, the description
     */
    @SqlUpdate("insert into feedback (user_id, rating, feedback_description) values (:userId, :rating, :feedbackDescription) RETURNING *")
    Feedback create(@Bind("userId") int userId, @Bind("rating") int rating, @Bind("feedbackDescription") String feedbackDescription);

    /**
     * Retrieves a single feedback based on the unique id assigned to each feedback.
     *
     * @param id an individual feedback id
     * @return the feedback with the id that matches the given id
     */
    @SqlQuery("select * from feedback where id = :id")
    Feedback retrieveOne(@Bind("id") int id);

    /**
     * Retrieves a list of feedback for any given user based on the given userId.
     *
     * @param userId the id of the user we want feedback for
     * @return the list of feedback left for a given user
     */
    @SqlQuery("select id, rating, feedback_description, time from feedback where user_id = :userId order by time desc")
    List<Feedback> retrieve(@Bind("userId") int userId);

    /**
     * Given a feedback, delete it from the database.
     *
     * @param feedback the feedback to delete
     */
    @SqlUpdate("delete from feedback where id = :id")
    void delete(@BindBean Feedback feedback);


}
