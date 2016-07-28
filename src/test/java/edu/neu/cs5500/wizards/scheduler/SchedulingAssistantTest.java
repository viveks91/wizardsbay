package edu.neu.cs5500.wizards.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Random;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WbaySchedulerFactory.class, LoggerFactory.class})
public class SchedulingAssistantTest {

    @Mock
    WbaySchedulerFactory wbaySchedulerFactory;

    @Mock
    Scheduler scheduler;

    @Mock
    Logger logger;

    String randomDate = "2016-07-27 03:03:03";

    @Before
    public void setUp() throws SchedulerException {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(WbaySchedulerFactory.class);
        PowerMockito.mockStatic(LoggerFactory.class);
        when(wbaySchedulerFactory.getScheduler()).thenReturn(scheduler);
        PowerMockito.when(WbaySchedulerFactory.getInstance()).thenReturn(wbaySchedulerFactory);
        PowerMockito.when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
    }

    @Test
    public void testCreateMessengerJob() throws SchedulerException {
        SchedulingAssistant schedulingAssistant = new SchedulingAssistant();
        schedulingAssistant.createNewMessengerJob(new Random().nextInt(), Timestamp.valueOf(this.randomDate));
    }

    @Test (expected = SchedulerException.class)
    public void testCreateMessengerJobException() throws SchedulerException {
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenThrow(new SchedulerException());
        when(wbaySchedulerFactory.getScheduler()).thenReturn(scheduler);
        PowerMockito.when(WbaySchedulerFactory.getInstance()).thenReturn(wbaySchedulerFactory);

        SchedulingAssistant schedulingAssistant = new SchedulingAssistant();
        schedulingAssistant.createNewMessengerJob(new Random().nextInt(), Timestamp.valueOf(this.randomDate));
    }

    @Test
    public void testUpdateMessengerJob() throws SchedulerException {
        SchedulingAssistant schedulingAssistant = new SchedulingAssistant();
        schedulingAssistant.updateMessengerJob(new Random().nextInt(), Timestamp.valueOf(this.randomDate));
    }

    @Test (expected = SchedulerException.class)
    public void testUpdateMessengerJobException() throws SchedulerException {
        when(scheduler.rescheduleJob(any(TriggerKey.class), any(Trigger.class))).thenThrow(new SchedulerException());
        when(wbaySchedulerFactory.getScheduler()).thenReturn(scheduler);
        PowerMockito.when(WbaySchedulerFactory.getInstance()).thenReturn(wbaySchedulerFactory);

        SchedulingAssistant schedulingAssistant = new SchedulingAssistant();
        schedulingAssistant.updateMessengerJob(new Random().nextInt(), Timestamp.valueOf(this.randomDate));
    }
}
