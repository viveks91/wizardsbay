package com.example.helloworld.mapper;

import com.example.helloworld.core.Item;
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
        item.setItemName(r.getString("itemname"));
        item.setItemDescription(r.getString("itemdescription"));
        item.setSellerId(r.getInt("sellerid"));
        item.setAuctionStartTime(r.getString("auctionstarttime"));
        item.setAuctionEndTime(r.getString("auctionendtime"));
        item.setMinBidAmount(r.getInt("minbidamount"));
        item.setCurrentMaxBid(r.getInt("currentbid"));

        return item;
    }
}

