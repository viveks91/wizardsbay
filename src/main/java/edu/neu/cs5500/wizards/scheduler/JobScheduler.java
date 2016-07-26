package edu.neu.cs5500.wizards.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
    private static JobScheduler jobScheduler = null;
    private Scheduler scheduler = null;

    private JobScheduler() {
    }

    public static JobScheduler getInstance() {
        if (jobScheduler == null) {
            jobScheduler = new JobScheduler();
        }

        return jobScheduler;
    }

    private void initiateScheduler() {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            this.scheduler = sf.getScheduler();
            this.scheduler.start();
            LOGGER.info("Scheduler started");
        } catch (SchedulerException e) {
            LOGGER.error("Scheduler failed to start", e);
        }
    }

    public Scheduler getScheduler() {
        if (this.scheduler == null) {
            this.initiateScheduler();
        }
        return this.scheduler;
    }

    public void shutdownScheduler() {
        try {
            this.scheduler.shutdown();
        } catch (SchedulerException e) {
            LOGGER.error("Scheduler failed to shutdown", e);
        }
    }

    public Scheduler renewScheduler() {
        this.initiateScheduler();
        return this.scheduler;
    }
}
