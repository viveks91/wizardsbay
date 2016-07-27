package edu.neu.cs5500.wizards.scheduler;

import edu.neu.cs5500.wizards.scheduler.jobs.Messenger;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobHelper.class);

    private JobScheduler jobScheduler;
    private final Scheduler scheduler;

    public JobHelper() {
        this.jobScheduler = JobScheduler.getInstance();
        this.scheduler = this.jobScheduler.getScheduler();
    }

    public void createNewMessengerJob(int itemId, Timestamp time) {
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
        }
    }

    public void updateMessengerJob(int itemId, Timestamp newTime) {
        Trigger trigger = newTrigger()
                .withIdentity("trigger" + itemId, "active")
                .startAt(newTime)
                .build();

        try {
            scheduler.rescheduleJob(TriggerKey.triggerKey("trigger" + itemId, "active"), trigger);
        } catch (SchedulerException e) {
            LOGGER.error("Failed to update the job", e);
        }
    }
}
