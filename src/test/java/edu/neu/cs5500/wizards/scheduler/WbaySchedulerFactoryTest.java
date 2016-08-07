package edu.neu.cs5500.wizards.scheduler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.Scheduler;
import org.slf4j.LoggerFactory;

/**
 * Created by susannaedens on 8/6/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WbaySchedulerFactory.class, LoggerFactory.class})
public class WbaySchedulerFactoryTest {

    WbaySchedulerFactory wbaySchedulerFactory;

    @Before
    public void setUp() throws Exception {
        wbaySchedulerFactory = WbaySchedulerFactory.getInstance();
    }

    @Test
    public void getInstance() throws Exception {
        WbaySchedulerFactory instance = WbaySchedulerFactory.getInstance();
        Assert.assertEquals(instance, WbaySchedulerFactory.getInstance());
    }

    @Test
    public void getScheduler() throws Exception {
        Scheduler scheduler = wbaySchedulerFactory.getScheduler();
        Assert.assertEquals(scheduler, wbaySchedulerFactory.getScheduler());
        wbaySchedulerFactory.shutdownScheduler();
        Assert.assertTrue(wbaySchedulerFactory.getScheduler().isShutdown());
    }

    @Test
    public void shutdownScheduler() throws Exception {
        wbaySchedulerFactory.renewScheduler();
        wbaySchedulerFactory.shutdownScheduler();
        Assert.assertTrue(wbaySchedulerFactory.getScheduler().isShutdown());
    }

    @Test
    public void renewScheduler() throws Exception {
        Scheduler scheduler = wbaySchedulerFactory.renewScheduler();
        Assert.assertEquals(scheduler, wbaySchedulerFactory.renewScheduler());
        scheduler.shutdown();
        Assert.assertTrue(wbaySchedulerFactory.getScheduler().isShutdown());
    }


}