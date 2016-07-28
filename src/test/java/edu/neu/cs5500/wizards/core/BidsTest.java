package edu.neu.cs5500.wizards.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * Created by susannaedens on 7/20/16.
 */
public class BidsTest {

    Bid b1, b2, b3, b4, b5;

    @Before
    public void setUp() throws Exception {
        b1 = new Bid();
        b1.setId(1);
        b1.setBidderId(2);
        b1.setBidderUsername("scott");
        b1.setItemId(5);
        b1.setBidAmount(54);
        b1.setBidTime(new Timestamp(998325689907234L));

        b2 = new Bid();
        b2.setId(2);
        b2.setBidderId(4);
        b2.setBidderUsername("vivek");
        b2.setItemId(4);
        b2.setBidAmount(32);
        b2.setBidTime(new Timestamp(998981671263L));

        b3 = new Bid();
        b3.setId(3);
        b3.setBidderId(98);
        b3.setBidderUsername("amala");
        b3.setItemId(123);
        b3.setBidAmount(32);
        b3.setBidTime(new Timestamp(122147234L));

        b4 = b3;
        b5 = b3;
    }

    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals(b2.getId(), (Integer) 2);
    }

    @Test
    public void testSetId() throws Exception {
        b3.setId(54);
        Assert.assertEquals(b3.getId(), (Integer) 54);
    }

    @Test
    public void testGetBidderUsername() throws Exception {
        Assert.assertEquals(b2.getBidderUsername(), "vivek");
    }

    @Test
    public void testSetBidderUsername() throws Exception {
        b1.setBidderUsername("susanna9");
        Assert.assertEquals(b1.getBidderUsername(), "susanna9");
    }

    @Test
    public void testGetItemId() throws Exception {
        Assert.assertEquals(b1.getItemId(), (Integer) 5);
    }

    @Test
    public void testSetItemId() throws Exception {
        b2.setItemId(6);
        Assert.assertEquals(b2.getItemId(), (Integer) 6);
    }

    @Test
    public void testGetBidderId() throws Exception {
        Assert.assertEquals(b1.getBidderId(), (Integer) 2);
    }

    @Test
    public void testSetBidderId() throws Exception {
        b3.setBidderId(66);
        Assert.assertEquals(b3.getBidderId(), (Integer) 66);
    }

    @Test
    public void testGetBidTime() throws Exception {
        Assert.assertEquals(b3.getBidTime(), new Timestamp(122147234L));
    }

    @Test
    public void testSetBidTime() throws Exception {
        b2.setBidTime(new Timestamp(88722L));
        Assert.assertEquals(b2.getBidTime(), new Timestamp(88722L));
    }

    @Test
    public void testGetBidAmount() throws Exception {
        Assert.assertEquals(b1.getBidAmount(), (Integer) 54);
    }

    @Test
    public void testSetBidAmount() throws Exception {
        b2.setBidAmount(776);
        Assert.assertEquals(b2.getBidAmount(), (Integer) 776);
    }

    @Test
    public void equals() throws Exception {
        Assert.assertEquals(b1, b1);
        Assert.assertNotEquals(b1, b2);
        Assert.assertTrue(b3.equals(b4) && b4.equals(b5));
        Assert.assertNotEquals(null, b1);
    }


    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(b1.hashCode(), b1.hashCode());
        Assert.assertEquals(b3.hashCode(), b5.hashCode());
        Assert.assertEquals(b4.hashCode(), b5.hashCode());
        Assert.assertEquals(b3.hashCode(), b4.hashCode());
        Assert.assertNotEquals(b1.hashCode(), b2.hashCode());
    }

}