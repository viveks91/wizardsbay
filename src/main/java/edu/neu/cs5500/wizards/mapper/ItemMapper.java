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
        item.setItemName(r.getString("itemName"));
        item.setItemDescription(r.getString("itemDescription"));
        item.setBuyerId(r.getInt("buyerId"));
        item.setSellerId(r.getInt("sellerId"));
        item.setAuctionStartTime(r.getTimestamp("auctionStartTime"));
        item.setAuctionEndTime(r.getTimestamp("auctionEndTime"));
        item.setMinBidAmount(r.getInt("minBidAmount"));

        return item;
    }
}

