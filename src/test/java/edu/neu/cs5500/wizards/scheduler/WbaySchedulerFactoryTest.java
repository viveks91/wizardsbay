package edu.neu.cs5500.wizards.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class, WbaySchedulerFactory.class})
public class WbaySchedulerFactoryTest {

    @Mock
    Logger logger;

    @Mock
    Scheduler scheduler;

    @Mock
    StdSchedulerFactory stdSchedulerFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(LoggerFactory.class);

        PowerMockito.when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
    }

    @Test
    public void testGetJobScheduler() throws Exception {
        when(stdSchedulerFactory.getScheduler()).thenReturn(scheduler);
        PowerMockito.whenNew(StdSchedulerFactory.class).withAnyArguments().thenReturn(stdSchedulerFactory);

        WbaySchedulerFactory wbaySchedulerFactory = WbaySchedulerFactory.getInstance();
        assertEquals(WbaySchedulerFactory.class, wbaySchedulerFactory.getClass());
    }

    /*  Unstable tests!
    @Test
    public void testGetScheduler() throws Exception {
        when(stdSchedulerFactory.getScheduler()).thenReturn(scheduler);
        PowerMockito.whenNew(StdSchedulerFactory.class).withAnyArguments().thenReturn(stdSchedulerFactory);

        WbaySchedulerFactory wbaySchedulerFactory = WbaySchedulerFactory.getInstance();
        Scheduler scheduler1 = wbaySchedulerFactory.getScheduler();

        assertEquals(scheduler, scheduler1);
    }

    @Test (expected = SchedulerException.class)
    public void testGetSchedulerException() throws Exception {
        when(stdSchedulerFactory.getScheduler()).thenThrow(new SchedulerException());
        PowerMockito.whenNew(StdSchedulerFactory.class).withAnyArguments().thenReturn(stdSchedulerFactory);

        WbaySchedulerFactory wbaySchedulerFactory = WbaySchedulerFactory.getInstance();
        Scheduler scheduler1 = wbaySchedulerFactory.getScheduler();
    }
    */

    @Test
    public void testShutDownScheduler() throws Exception {
        when(stdSchedulerFactory.getScheduler()).thenReturn(scheduler);
        PowerMockito.whenNew(StdSchedulerFactory.class).withAnyArguments().thenReturn(stdSchedulerFactory);

        WbaySchedulerFactory wbaySchedulerFactory = WbaySchedulerFactory.getInstance();
        wbaySchedulerFactory.getScheduler();
        wbaySchedulerFactory.shutdownScheduler();
    }

    @Test
    public void testRenewScheduler() throws Exception {
        when(stdSchedulerFactory.getScheduler()).thenReturn(scheduler);
        PowerMockito.whenNew(StdSchedulerFactory.class).withAnyArguments().thenReturn(stdSchedulerFactory);

        WbaySchedulerFactory wbaySchedulerFactory = WbaySchedulerFactory.getInstance();
        wbaySchedulerFactory.renewScheduler();
    }
}
