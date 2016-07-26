/**
 * Created by amala on 16/06/16.
 */

package edu.neu.cs5500.wizards.db;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.mapper.ItemMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.Timestamp;
import java.util.List;

@RegisterMapper(ItemMapper.class)
public interface ItemDAO {


    /**
     * Inserts the given item into the database.
     *
     * @param item the item to create
     * @return the created item
     */
    @SqlQuery("insert into items (item_name, item_description, seller_id, auction_end_time, min_bid_amount) values (:itemName, :itemDescription, :sellerId, :auctionEndTime, :minBidAmount) RETURNING *")
    Item create(@BindBean Item item);


    /**
     * Given an id of a seller, returns all items (active & inactive) belonging to that seller.
     *
     * @param sellerId the id of the seller
     * @return a list of the seller's items
     */
    @SqlQuery("select * from items where seller_id = :sellerId")
    List<Item> findItemsBySellerId(@Bind("sellerId") int sellerId);


    /**
     * Given the id of an item, return that item.
     *
     * @param itemId the item id
     * @return the item corresponding to the given id
     */
    @SqlQuery("select * from items where id = :itemId")
    Item findItemById(@Bind("itemId") int itemId);


    /**
     * Updates the item in the database where you can update the following fields: item name, item description,
     * auction end time and minimum bid amount.
     *
     * @param itemId          the id of the item to update
     * @param itemName        the new name for the item
     * @param itemDescription the item's description
     * @param auctionEndTime  the end time of the auction
     * @param minBidAmount    the minimum bid amount
     */
    @SqlUpdate("update items set item_name = :itemName, item_description = :itemDescription, auction_end_time = :auctionEndTime, min_bid_amount = :minBidAmount where id = :itemId")
    void update(@Bind("itemId") int itemId, @Bind("itemName") String itemName, @Bind("itemDescription") String itemDescription, @Bind("auctionEndTime") Timestamp auctionEndTime, @Bind("minBidAmount") int minBidAmount);


    /**
     * Updates the buyer information for a given item given the item id, the buyer's if and the new bid amount.
     *
     * @param itemId       the id of the item to update
     * @param bidderId     the id of the current winning bidder
     * @param minBidAmount the new bid amount for the item
     */
    @SqlUpdate("update items set buyer_id = :bidderId, min_bid_amount = :minBidAmount where id = :itemId")
    void updateBuyerInfo(@Bind("itemId") int itemId, @Bind("bidderId") int bidderId, @Bind("minBidAmount") int minBidAmount);


    /**
     * Given an item id, delete the corresponding item from the database.
     *
     * @param itemId the id of the item.
     */
    @SqlUpdate("delete from items where id = :itemId")
    void deleteItem(@Bind("itemId") int itemId);


    /**
     * Returns all active items from the database. Active means that the auction is not yet over and the item is still
     * available to be bid on.
     *
     * @return a list of all active items
     */
    @SqlQuery("select * from items where auction_end_time > localtimestamp")
    List<Item> findAllActiveItems();
    
    /**
     * Returns all active items from the database. With name that is LIKE search string
     *      *
     * @return a list of matching active items
     */
    @SqlQuery("select * from items where auction_end_time > localtimestamp AND itemName LIKE '%:search%' OR itemDescription LIKE %:search%")
    List<Item> searchItems(@Bind("search") String search);

}
