package com.example.helloworld.db;

import com.example.helloworld.core.Bid;
import com.example.helloworld.core.User;
import com.example.helloworld.mapper.BidMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by susannaedens on 6/20/16.
 */

@RegisterMapper(BidMapper.class)
public interface BidDAO {

    @SqlUpdate("insert into bids (itemId, bidder, bidAmount) values (:itemId, :bidder, :bidAmount)")
    public void create(@Bind("itemId") int itemId, @Bind("bidder") int bidder, @Bind("bidAmount") int bidAmount);

    //> retrieve by itemId because each item only has one bid? Or if we're creating bids for each new bid use
    //> the method below.
    @SqlQuery("select id, itemId, bidder, bidAmount from bids where itemId = :itemId")
    public Bid retrieve(@Bind("itemId") int itemId);

    //> method to retrieve highest bid for a given itemId
    @SqlQuery("with items as (select * from bids where itemId = :itemId) select * from items order by bidAmount desc limit 1")
    public Bid retrieveHighestBid(@Bind("itemId") int itemId);

    //return the history of bids for a certain item
    @SqlQuery("select * from bids where itemid = :itemid")
    public List<Bid> findBidsByItemId(@Bind("itemid") int itemid);

    //find bid by id
    @SqlQuery("select * from bids where id = :id")
    public Bid findItemById(@Bind("id") int id);

    //> Delete bid by unique id
    @SqlUpdate("delete from bids where id = :id")
    public void delete(@BindBean Bid bid);

}
