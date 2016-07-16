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

    //Create new item
    @SqlQuery("insert into items (item_name, item_description, seller_id, auction_end_time, min_bid_amount) values (:itemName, :itemDescription, :sellerId, :auctionEndTime, :minBidAmount) RETURNING *")
    Item create(@BindBean Item item);

    //Items listed by a particular seller
    @SqlQuery("select * from items where seller_id = :sellerId")
    List<Item> findItemsBySellerId(@Bind("sellerId") int sellerId);

    //item by item Id
    @SqlQuery("select * from items where id = :itemId")
    Item findItemById(@Bind("itemId") int itemId);

    //update item details and auction end time for the item by item Id
    @SqlUpdate("update items set item_name = :itemName, item_description = :itemDescription, auction_end_time = :auctionEndTime, min_bid_amount = :minBidAmount where id = :itemId")
    void update(@Bind("itemId") int itemId, @Bind("itemName") String itemName, @Bind("itemDescription") String itemDescription, @Bind("auctionEndTime") Timestamp auctionEndTime, @Bind("minBidAmount") int minBidAmount);

    //update when a new bid is made for the item
    @SqlUpdate("update items set buyer_id = :bidderId, min_bid_amount = :minBidAmount where id = :itemId")
    void updateBuyerInfo(@Bind("itemId") int itemId, @Bind("bidderId") int bidderId, @Bind("minBidAmount") int minBidAmount);

    //delete item by item Id
    //TODO : delete should be possible only by item owners/sellers
    @SqlUpdate("delete from items where id = :itemId")
    void deleteItem(@Bind("itemId") int itemId);

    //select all active items
    @SqlQuery("select * from items where auction_end_time > localtimestamp")
    List<Item> findAllActiveItems();

}
