package edu.neu.cs5500.wizards.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * Created by susannaedens on 7/19/16.
 */
public class FeedbackTest {

    Feedback f1, f2, f3, f4, f5;
    Timestamp t1;

    @Before
    public void testSetUp() throws Exception {
        t1 = new Timestamp(324234434343444L);
        f1 = new Feedback();
        f1.setId(1);
        f1.setUserId(2);
        f1.setRating(5);
        f1.setUsername("Susanna9");
        f1.setFeedbackDescription("Great seller!");
        f1.setTime(t1);

        f2 = new Feedback();
        f2.setId(2);
        f2.setUserId(3);
        f2.setRating(1);
        f2.setUsername("Vivek");
        f2.setFeedbackDescription("Terrible seller, do not buy from");
        f2.setTime(t1);

        f3 = new Feedback();
        f3.setId(3);
        f3.setUserId(2);
        f3.setRating(5);
        f3.setUsername("Amala");
        f3.setFeedbackDescription("Fantastic transaction");
        f3.setTime(t1);

        f4 = f3;
        f5 = f3;
    }

    @Test
    public void testGetRating() throws Exception {
        Assert.assertEquals(f1.getRating(), (Integer) 5);
    }

    @Test
    public void testSetRating() throws Exception {
        f1.setRating(4);
        Assert.assertEquals(f1.getRating(), (Integer) 4);
    }

    @Test
    public void testGetUsername() throws Exception {
        Assert.assertEquals(f1.getUsername(), "Susanna9");
    }

    @Test
    public void testSetUsername() throws Exception {
        f1.setUsername("Susanna92");
        Assert.assertEquals(f1.getUsername(), "Susanna92");
    }

    @Test
    public void testGetTime() throws Exception {
        Assert.assertEquals(f1.getTime(), t1);
    }

    @Test
    public void testSetTime() throws Exception {
        f1.setTime(new Timestamp(4));
        Assert.assertEquals(f1.getTime(), new Timestamp(4));
    }

    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals(f1.getId(), (Integer) 1);
    }

    @Test
    public void testSetId() throws Exception {
        f1.setId(2);
        Assert.assertEquals(f1.getId(), (Integer) 2);
    }

    @Test
    public void testGetUserId() throws Exception {
        Assert.assertEquals(f1.getUserId(), (Integer) 2);
    }

    @Test
    public void testSetUserId() throws Exception {
        f1.setUserId(5);
        Assert.assertEquals(f1.getUserId(), (Integer) 5);
    }

    @Test
    public void testGetFeedbackDescription() throws Exception {
        Assert.assertEquals(f1.getFeedbackDescription(), "Great seller!");
    }

    @Test
    public void testSetFeedbackDescription() throws Exception {
        f1.setFeedbackDescription("Changed my mind, this seller is fantastic!");
        Assert.assertEquals(f1.getFeedbackDescription(), "Changed my mind, this seller is fantastic!");
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(f1, f1);
        Assert.assertNotEquals(f1, f2);
        Assert.assertTrue(f3.equals(f4) && f4.equals(f5));
        Assert.assertNotEquals(null, f1);
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(f1.hashCode(), f1.hashCode());
        Assert.assertEquals(f3.hashCode(), f5.hashCode());
        Assert.assertEquals(f4.hashCode(), f5.hashCode());
        Assert.assertEquals(f3.hashCode(), f4.hashCode());
        Assert.assertNotEquals(f1.hashCode(), f2.hashCode());
    }
    
}