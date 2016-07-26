package edu.neu.cs5500.wizards.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by susannaedens on 7/20/16.
 */
public class UserTest {
    
    User u1, u2, u3, u4, u5;
    
    @Before
    public void setUp() throws Exception {
        u1 = new User();
        u1.setId(1);
        u1.setUsername("bobbybrown");
        u1.setPassword("securePassword");
        u1.setFirstName("Robert");
        u1.setLastName("Brown");
        u1.setAddress("45 main st seattle, wa");
        u1.setEmail("bobby231@yahoo.com");

        u2 = new User();
        u2.setId(2);
        u2.setUsername("skoobydoo");
        u2.setPassword("biscuits");
        u2.setFirstName("Scoobert");
        u2.setLastName("Dog");
        u2.setAddress("woof woof lane seattle, wa");
        u2.setEmail("scoobyscooby2@gmail.com");

        u3 = new User();
        u3.setId(3);
        u3.setUsername("frankiestein");
        u3.setPassword("monstaa");
        u3.setFirstName("Francesca");
        u3.setLastName("Steinberg");
        u3.setAddress("98-21 Vivace Terrace Boolean, SD");
        u3.setEmail("frankieG@gmail.com");

        u4 = u3;
        u5 = u3;
    }

    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals(u1.getId(), (Integer) 1);
    }

    @Test
    public void testSetId() throws Exception {
        u3.setId(99);
        Assert.assertEquals(u3.getId(), (Integer) 99);
    }

    @Test
    public void testGetEmail() throws Exception {
        Assert.assertEquals(u4.getEmail(), "frankieG@gmail.com");
    }

    @Test
    public void testSetEmail() throws Exception {
        u5.setEmail("simmiesimms9@hotmail.com");
        Assert.assertEquals(u5.getEmail(), "simmiesimms9@hotmail.com");
    }

    @Test
    public void testGetUsername() throws Exception {
        Assert.assertEquals(u2.getUsername(), "skoobydoo");
    }

    @Test
    public void testSetUsername() throws Exception {
        u1.setUsername("helloworld");
        Assert.assertEquals(u1.getUsername(), "helloworld");
    }

    @Test
    public void testGetPassword() throws Exception {
        Assert.assertEquals(u3.getPassword(), "monstaa");
    }

    @Test
    public void testSetPassword() throws Exception {
        u2.setPassword("goodstuff");
        Assert.assertEquals(u2.getPassword(), "goodstuff");
    }

    @Test
    public void testGetFirstName() throws Exception {
        Assert.assertEquals(u4.getFirstName(), "Francesca");
    }

    @Test
    public void testSetFirstName() throws Exception {
        u5.setFirstName("Frankie");
        Assert.assertEquals(u5.getFirstName(), "Frankie");
    }

    @Test
    public void testGetLastName() throws Exception {
        Assert.assertEquals(u1.getLastName(), "Brown");
    }

    @Test
    public void testSetLastName() throws Exception {
        u3.setLastName("Stacy");
        Assert.assertEquals(u3.getLastName(), "Stacy");
    }

    @Test
    public void testGetAddress() throws Exception {
        Assert.assertEquals(u2.getAddress(), "woof woof lane seattle, wa");
    }

    @Test
    public void testSetAddress() throws Exception {
        u1.setAddress("782-3 Apt C DogHouse Lane Boston, MA");
        Assert.assertEquals(u1.getAddress(), "782-3 Apt C DogHouse Lane Boston, MA");
    }

    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(u1, u1);
        Assert.assertNotEquals(u1, u2);
        Assert.assertTrue(u3.equals(u4) && u4.equals(u5));
        Assert.assertNotEquals(null, u1);
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertEquals(u1.hashCode(), u1.hashCode());
        Assert.assertEquals(u3.hashCode(), u5.hashCode());
        Assert.assertEquals(u4.hashCode(), u5.hashCode());
        Assert.assertEquals(u3.hashCode(), u4.hashCode());
        Assert.assertNotEquals(u1.hashCode(), u2.hashCode());
    }
    
}