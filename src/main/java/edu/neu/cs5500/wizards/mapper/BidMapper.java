package edu.neu.cs5500.wizards.mapper;

import edu.neu.cs5500.wizards.core.Bid;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by susannaedens on 6/20/16.
 */
public class BidMapper implements ResultSetMapper<Bid> {

    @Override
    public Bid map(int index, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        Bid bid = new Bid();
        bid.setId(resultSet.getInt("id"));
        bid.setItemId(resultSet.getInt("item_id"));
        bid.setBidderId(resultSet.getInt("bidder_id"));
        bid.setBidAmount(resultSet.getInt("bid_amount"));
        bid.setBidTime(resultSet.getTimestamp("bid_time"));
        return bid;
    }

}
