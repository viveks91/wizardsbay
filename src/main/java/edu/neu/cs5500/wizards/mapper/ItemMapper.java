package edu.neu.cs5500.wizards.mapper;

import edu.neu.cs5500.wizards.core.Item;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by amala on 16/06/16.
 */
public class ItemMapper implements ResultSetMapper<Item> {
    public Item map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Item item = new Item();
        item.setId(r.getInt("id"));
        item.setItemName(r.getString("item_name"));
        item.setItemDescription(r.getString("item_description"));
        item.setBuyerId(r.getInt("buyer_id"));
        item.setSellerId(r.getInt("seller_id"));
        item.setAuctionStartTime(r.getTimestamp("auction_start_time"));
        item.setAuctionEndTime(r.getTimestamp("auction_end_time"));
        item.setMinBidAmount(r.getInt("min_bid_amount"));

        try {
            item.setSellerUsername(r.getString("username"));
        } catch (SQLException ignored) {
        }

        return item;
    }
}

