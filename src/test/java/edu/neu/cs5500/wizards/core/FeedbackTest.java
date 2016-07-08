package edu.neu.cs5500.wizards.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by susannaedens on 7/8/16.
 */
public class FeedbackTest {

    Feedback f1, f2, f3, f4, f5;

    @Before
    public void setUp() throws Exception {
        f1 = new Feedback();
        f1.setId(4);
        f1.setUserid(9);
        f1.setFeedbackdesc("Great seller. Would buy from again.");
        f2 = new Feedback();
        f3 = new Feedback();
        f2.setId(4);
        f2.setUserid(9);
        f2.setFeedbackdesc("Great seller. Would buy from again.");
        f3.setId(4);
        f3.setUserid(9);
        f3.setFeedbackdesc("Great seller. Would buy from again.");
        f4 = new Feedback();
        f4.setId(5);
        f4.setUserid(10);
        f4.setFeedbackdesc("Pleasant transaction.");
        f5 = new Feedback();
    }

    @Test
    public void getId() throws Exception {
        Assert.assertEquals(f1.getId(), 4);
        Assert.assertEquals(f4.getId(), 5);
        Assert.assertEquals(f1.getId(), f3.getId());
    }

    @Test
    public void setId() throws Exception {
        f1.setId(6);
        Assert.assertEquals(f1.getId(), 6);
        Assert.assertNotEquals(f2.getId(), f1.getId());
    }
    

    @Test
    public void getUserid() throws Exception {
        Assert.assertEquals(f2.getUserid(), 9);
        Assert.assertEquals(f2.getUserid(), f3.getUserid());
    }

    @Test
    public void setUserid() throws Exception {
        f3.setUserid(12);
        Assert.assertEquals(f3.getUserid(), 12);
        Assert.assertNotEquals(f2.getUserid(), f3.getUserid());
    }

    @Test
    public void getFeedbackdesc() throws Exception {
        Assert.assertEquals(f2.getFeedbackdesc(), "Great seller. Would buy from again.");
        Assert.assertEquals(f3.getFeedbackdesc(), f2.getFeedbackdesc());
        Assert.assertEquals(f4.getFeedbackdesc(), "Pleasant transaction.");
    }

    @Test
    public void setFeedbackdesc() throws Exception {
        f3.setFeedbackdesc("Horrible seller");
        Assert.assertEquals(f3.getFeedbackdesc(), "Horrible seller");
        Assert.assertNotEquals(f3.getFeedbackdesc(), f2.getFeedbackdesc());
        Assert.assertNotEquals(f3.getFeedbackdesc(), f4.getFeedbackdesc());
    }

    @Test
    public void hashCodeTest() throws Exception {
        Assert.assertEquals(f1.hashCode(), f1.hashCode());
        Assert.assertEquals(f1.hashCode(), f2.hashCode());
        Assert.assertTrue(f1.hashCode() == f2.hashCode() && f2.hashCode() == f3.hashCode());
        Assert.assertFalse(f1.hashCode() == f4.hashCode() && f1.hashCode() == f5.hashCode());
        Feedback test = new Feedback();
        Assert.assertEquals(f5.hashCode(), test.hashCode());
    }

    @Test
    public void equals() throws Exception {
        Assert.assertEquals(f1, f1);
        Assert.assertTrue(f1.equals(f3) && f2.equals(f3));
        Assert.assertNotEquals(f1, f4);
        Assert.assertNotEquals(f4, f5);
        Assert.assertNotEquals(f1, null);
        Feedback test = new Feedback();
        Assert.assertEquals(f5, test);
        f3.setId(5);
        Assert.assertNotEquals(f3, f4);
        f3.setUserid(10);
        Assert.assertNotEquals(f3, f4);
        Bid test2 = new Bid();
        Assert.assertFalse(f1.equals(test2));
    }

    @Test
    public void toStringTest() throws Exception {
        Assert.assertEquals(f1.toString(), "Feedback[id=4,userid=9,feedbackdesc=Great seller. Would buy from again.]");
        Assert.assertEquals(f4.toString(), "Feedback[id=5,userid=10,feedbackdesc=Pleasant transaction.]");
        Assert.assertEquals(f5.toString(), "Feedback[id=0,userid=0,feedbackdesc=<null>]");
    }
    
}