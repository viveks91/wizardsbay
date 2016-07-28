package edu.neu.cs5500.wizards.scheduler;

import edu.neu.cs5500.wizards.scheduler.jobs.Messenger;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class SchedulingAssistant {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingAssistant.class);

    private final Scheduler scheduler;

    public SchedulingAssistant() throws SchedulerException {
        this.scheduler = WbaySchedulerFactory.getInstance().getScheduler();
    }

    public void createNewMessengerJob(int itemId, Timestamp time) throws SchedulerException {
        JobDetail job = newJob(Messenger.class)
                .withIdentity("job" + itemId, "active")
                .build();
        job.getJobDataMap().put("itemId", itemId);

        Trigger trigger = newTrigger()
                .withIdentity("trigger" + itemId, "active")
                .startAt(time)
                .build();
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOGGER.error("Failed to schedule a job", e);
            throw e;
        }
    }

    public void updateMessengerJob(int itemId, Timestamp newTime) throws SchedulerException {
        Trigger trigger = newTrigger()
                .withIdentity("trigger" + itemId, "active")
                .startAt(newTime)
                .build();
        try {
            scheduler.rescheduleJob(TriggerKey.triggerKey("trigger" + itemId, "active"), trigger);
        } catch (SchedulerException e) {
            LOGGER.error("Failed to update the job", e);
            throw e;
        }
    }
}
