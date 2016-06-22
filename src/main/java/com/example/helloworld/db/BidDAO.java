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
    public void create(@Bind("itemId") int itemId, @Bind("bidder") int bidder, @Bind("bidAmount") BigDecimal bidAmount);


    //> retrieve by itemId because each item only has one bid? Or if we're creating bids for each new bid use
    //> the method below.
    @SqlQuery("select id, itemId, bidder, bidAmount from bids where itemId = :itemId")
    public Bid retrieve(@Bind("itemId") int itemId);

//    @SqlQuery("select id, bidder, bidAmount from bids where itemId = :itemId")
//    public Set<Bid> retrieve(@Bind("itemId")int itemId);


    //> Should we update the bid if there's a new higher bidder or do we just create a new one?
    //> If we create a new one there should be no reason to have an update method...
    @SqlUpdate("update bids set bidder = :bidder, bidAmount = :bidAmount where itemId = :itemId")
    public void update(@Bind("bidder") int bidder, @Bind("bidAmount") BigDecimal bidAmount);

    //> Delete bid by unique id
    @SqlUpdate("delete from bids where id = :id")
    public void delete(@BindBean Bid bid);

    //> purpose of this method?
    @SqlQuery("select id, itemId, bidder, bidAmount from bids limit :limit offset :offset")
    public Set<User> list(@Bind("limit") int limit, @Bind("offset") int offset);

    //return the history of bids for a certain item
    @SqlQuery("select * from bids where itemid = :itemid")
    public List<Bid> findBidsByItemId(@Bind("itemid")int itemid);

    //find bid by id
    @SqlQuery("select * from bids where id = :id")
    public Bid findItemById(@Bind("id") int id);


}
