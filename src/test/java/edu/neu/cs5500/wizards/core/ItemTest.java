package edu.neu.cs5500.wizards.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * Created by susannaedens on 7/20/16.
 */
public class ItemTest {
    
    Item i1, i2, i3, i4, i5;
    
    @Before
    public void setUp() throws Exception {
        i1 = new Item();
        i1.setId(1);
        i1.setItemName("Black shoes size 9");
        i1.setItemDescription("New black shoes, good for work");
        i1.setSellerId(3);
        i1.setSellerUsername("stacy9");
        i1.setBuyerId(4);
        i1.setBuyerUsername("george42");
        i1.setAuctionStartTime(new Timestamp(34555522L));
        i1.setAuctionEndTime(new Timestamp(999927743L));
        i1.setMinBidAmount(55);

        i2 = new Item();
        i2.setId(2);
        i2.setItemName("Unbearable lightness of being");
        i2.setItemDescription("Philosophical book by Milan Kundera");
        i2.setSellerId(9);
        i2.setSellerUsername("freddybanks");
        i2.setBuyerId(2);
        i2.setBuyerUsername("bobbybob");
        i2.setAuctionStartTime(new Timestamp(22998522L));
        i2.setAuctionEndTime(new Timestamp(584927743L));
        i2.setMinBidAmount(5);
        

        i3 = new Item();
        i3.setId(2);
        i3.setItemName("Ipod shuffle");
        i3.setItemDescription("1gb shuffle in good shape");
        i3.setSellerId(87);
        i3.setSellerUsername("sillysun72");
        i3.setBuyerId(33);
        i3.setBuyerUsername("grapesofgrapes");
        i3.setAuctionStartTime(new Timestamp(667998522L));
        i3.setAuctionEndTime(new Timestamp(112327743L));
        i3.setMinBidAmount(27);
        
        i4 = i3;
        i5 = i3;
    }

    @Test
    public void testGetBuyerUsername() throws Exception {
        Assert.assertEquals(i1.getBuyerUsername(), "george42");
    }

    @Test
    public void testSetBuyerUsername() throws Exception {
        i3.setBuyerUsername("yellowball");
        Assert.assertEquals(i3.getBuyerUsername(), "yellowball");
    }

    @Test
    public void testGetSellerUsername() throws Exception {
        Assert.assertEquals(i2.getSellerUsername(), "freddybanks");
    }

    @Test
    public void testSetSellerUsername() throws Exception {
        i1.setSellerUsername("susanna9");
        Assert.assertEquals(i1.getSellerUsername(), "susanna9");
    }

    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals(i2.getId(), (Integer) 2);
    }

    @Test
    public void testSetId() throws Exception {
        i4.setId(42);
        Assert.assertEquals(i4.getId(), (Integer) 42);
    }

    @Test
    public void testGetItemName() throws Exception {
        Assert.assertEquals(i1.getItemName(), "Black shoes size 9");
    }

    @Test
    public void testSetItemName() throws Exception {
        i3.setItemName("philosophical book");
        Assert.assertEquals(i3.getItemName(), "philosophical book");
    }

    @Test
    public void testGetItemDescription() throws Exception {
        Assert.assertEquals(i4.getItemDescription(), "1gb shuffle in good shape");
    }

    @Test
    public void testSetItemDescription() throws Exception {
        i5.setItemDescription("brand new ipod nano");
        Assert.assertEquals(i4.getItemDescription(), "brand new ipod nano");
    }

    @Test
    public void testGetSellerId() throws Exception {
        Assert.assertEquals(i2.getSellerId(), (Integer) 9);
    }

    @Test
    public void testSetSellerId() throws Exception {
        i3.setSellerId(66);
        Assert.assertEquals(i3.getSellerId(), (Integer) 66);
    }

    @Test
    public void testGetBuyerId() throws Exception {
        Assert.assertEquals(i5.getBuyerId(), (Integer) 33);
    }

    @Test
    public void testSetBuyerId() throws Exception {
        i2.setBuyerId(42);
        Assert.assertEquals(i2.getBuyerId(), (Integer) 42);
    }

    @Test
    public void testGetAuctionStartTime() throws Exception {
        Assert.assertEquals(i1.getAuctionStartTime(), new Timestamp(34555522L));
    }

    @Test
    public void testSetAuctionStartTime() throws Exception {
        i3.setAuctionStartTime(new Timestamp(3333L));
        Assert.assertEquals(i3.getAuctionStartTime(), new Timestamp(3333L));
    }

    @Test
    public void testGetAuctionEndTime() throws Exception {
        Assert.assertEquals(i4.getAuctionEndTime(), new Timestamp(112327743L));
    }

    @Test
    public void testSetAuctionEndTime() throws Exception {
        i2.setAuctionEndTime(new Timestamp(459288L));
        Assert.assertEquals(i2.getAuctionEndTime(), new Timestamp(459288L));
    }

    @Test
    public void testGetMinBidAmount() throws Exception {
        Assert.assertEquals(i1.getMinBidAmount(), (Integer) 55);
    }

    @Test
    public void testSetMinBidAmount() throws Exception {
        i3.setMinBidAmount(3);
        Assert.assertEquals(i3.getMinBidAmount(), (Integer) 3);
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(i1, i1);
        Assert.assertNotEquals(i1, i2);
        Assert.assertTrue(i3.equals(i4) && i4.equals(i5));
        Assert.assertNotEquals(null, i1);
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(i1.hashCode(), i1.hashCode());
        Assert.assertEquals(i3.hashCode(), i5.hashCode());
        Assert.assertEquals(i4.hashCode(), i5.hashCode());
        Assert.assertEquals(i3.hashCode(), i4.hashCode());
        Assert.assertNotEquals(i1.hashCode(), i2.hashCode());
    }
    
}