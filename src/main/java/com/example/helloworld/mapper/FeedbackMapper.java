package com.example.helloworld.mapper;

import com.example.helloworld.core.Feedback;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by susannaedens on 6/20/16.
 */
public class FeedbackMapper implements ResultSetMapper<Feedback> {

    @Override
    public Feedback map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(resultSet.getInt("id"));
        feedback.setUserid(resultSet.getInt("userid"));
        feedback.setFeedbackdesc(resultSet.getString("feedbackdesc"));
        System.out.println("in the feedback mapper");
        return feedback;
    }

}
