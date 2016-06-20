/**
 * Created by amala on 16/06/16.
 */

package com.example.helloworld.db;

import com.example.helloworld.core.Item;
import com.example.helloworld.core.User;
import com.example.helloworld.mapper.*;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.Timestamp;
import java.util.Set;
import java.util.List;

@RegisterMapper(ItemMapper.class)
public interface ItemDAO {

    //Create new item
    @SqlQuery("insert into items (itemname, itemdescription, sellerid, auctionstarttime, auctionendtime, minbidamount, currentbid) values (:itemName, :itemDescription, :sellerId, to_timestamp(:auctionStartTime, 'YYYY-MM-DD HH24:MI:SS')\\:\\:timestamp, to_timestamp(:auctionEndTime, 'YYYY-MM-DD HH24:MI:SS')\\:\\:timestamp, :minBidAmount, :currentMaxBid) RETURNING *")
    public Item create(@BindBean Item item);

    //Items listed by a particular seller
    @SqlQuery("select * from items where sellerid = :sellerid")
    public List<Item> findItemBySellerId(@Bind("sellerid") int sellerid);

    //item by item Id
    @SqlQuery("select * from items where id = :itemId")
    public Item findItemById(@Bind("itemId") int itemId);

    //update item details and auction end time for the item by item Id
    @SqlUpdate("update items set itemname = :itemname, itemdescription = :itemdescription, auctionendtime = :auctionendtime where id = :itemid")
    public void update(@Bind("itemid") String itemid, @Bind("itemname") String itemname, @Bind("itemdescription") String itemdescription, @Bind("auctionendtime") Timestamp auctionendtime);

    //delete item by item Id ****** TO DO -- delete should be possible only by item owners/sellers ******
    @SqlUpdate("delete from items where id = :id")
    public void deleteItem(@BindBean Item item);

    //select all item active
    @SqlQuery("select * from items where auctionendtime > localtimestamp")
    public List<Item> findAllActiveItems();


    @SqlQuery("select username, password, firstname, lastname, address from users limit :limit offset :offset")
    public Set<User> list(@Bind("limit") int limit, @Bind("offset") int offset);



}
