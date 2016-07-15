package edu.neu.cs5500.wizards.db;

import edu.neu.cs5500.wizards.core.Bid;
import edu.neu.cs5500.wizards.mapper.BidMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by susannaedens on 6/20/16.
 */

@RegisterMapper(BidMapper.class)
public interface BidDAO {

    /**
     * Creates a new Bid given the id of the item, the id of the bidder, and the bid amount. If the bidAmount is
     * not higher than the highest bid amount for the given item, the bid will not be created.
     *
     * @param itemId    the id of the item to bid on
     * @param bidderId    the id of the user bidding on the item
     * @param bidAmount the bid amount
     */
    @SqlUpdate("insert into bids (item_id, bidder_id, bid_amount) values (:itemId, :bidderId, :bidAmount) RETURNING *")
    Bid create(@Bind("itemId") int itemId, @Bind("bidderId") int bidderId, @Bind("bidAmount") int bidAmount);

    /**
     * Retrieves a single bid based on the bid's id.
     *
     * @param id the id of the bid
     * @return a bid with the id matching the given id
     */
    @SqlQuery("select * from bids where id = :id")
    Bid retrieve(@Bind("id") int id);

//    /**
//     * Retrieves the highest current bid on an item given the item's id.
//     *
//     * @param itemId the id of the item
//     * @return the current highest bid
//     */
//    @SqlQuery("with items as (select * from bids where itemId = :itemId) select * from items order by bidAmount desc limit 1")
//    Bid retrieveHighestBid(@Bind("itemId") int itemId);

    /**
     * Retrieve a list of bids representing the bid history for a certain item given the item's id.
     *
     * @param itemId the id of the item
     * @return a history of bids for a given item
     */
    @SqlQuery("select * from bids where item_id = :itemId order by bid_amount desc")
    List<Bid> findBidsByItemId(@Bind("itemId") int itemId);


    /**
     * Given a bid, delete it from the database.
     *
     * @param bid the bid to delete
     */
    @SqlUpdate("delete from bids where id = :id")
    void delete(@BindBean Bid bid);

}
