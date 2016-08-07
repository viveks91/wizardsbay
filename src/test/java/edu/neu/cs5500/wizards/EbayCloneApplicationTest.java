package edu.neu.cs5500.wizards;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by susannaedens on 8/6/16.
 */
public class EbayCloneApplicationTest {
    //> this test is for integration
    //> it uses a client to create & run our EbayCloneApplication
    //> it runs other tests and methods through this client and boosts our line coverage
    
    @ClassRule
    public static final DropwizardAppRule<ServiceConfiguration> RULE = new DropwizardAppRule<>(
            EbayCloneApplication.class, "configuration.yml");

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
        RULE.getApplication().run("db", "migrate", "configuration.yml");
    }

    @Test
    public void getName() throws Exception{
        Assert.assertEquals("ebay-clone", RULE.newApplication().getName());
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }
    
}