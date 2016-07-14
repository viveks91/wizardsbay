/**
 * Created by amala on 16/06/16.
 */

package edu.neu.cs5500.wizards.db;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.mapper.ItemMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@RegisterMapper(ItemMapper.class)
public interface ItemDAO {

    //Create new item
    @SqlQuery("insert into items (itemName, itemDescription, sellerId, auctionStartTime, auctionEndTime, minDidAmount) values (:itemName, :itemDescription, :sellerId, :auctionStartTime, :auctionEndTime, :minBidAmount) RETURNING *")
    Item create(@BindBean Item item);

    //Items listed by a particular seller
    @SqlQuery("select * from items where sellerId = :sellerId")
    List<Item> findItemsBySellerId(@Bind("sellerId") int sellerId);

    //item by item Id
    @SqlQuery("select * from items where id = :itemId")
    Item findItemById(@Bind("itemId") int itemId);

    //update item details and auction end time for the item by item Id
    @SqlUpdate("update items set itemName = :itemName, itemDescription = :itemDescription, auctionEndTime = :auctionEndTime where id = :itemId")
    void update(@Bind("itemId") String itemId, @Bind("itemName") String itemName, @Bind("itemDescription") String itemDescription, @Bind("auctionEndTime") Timestamp auctionEndTime);

    //delete item by item Id
    //TODO : delete should be possible only by item owners/sellers
    @SqlUpdate("delete from items where id = :id")
    void deleteItem(@BindBean Item item);

    //select all item active
    @SqlQuery("select * from items where auctionEndTime > localtimestamp")
    List<Item> findAllActiveItems();

}
