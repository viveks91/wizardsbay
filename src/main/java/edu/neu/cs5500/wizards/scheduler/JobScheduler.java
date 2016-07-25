package edu.neu.cs5500.wizards.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);
    private static Scheduler scheduler = null;

    private JobScheduler() {
    }

    private static void initiateScheduler() {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            scheduler = sf.getScheduler();
            scheduler.start();
            LOGGER.info("Scheduler started");
        } catch (SchedulerException e) {
            LOGGER.error("Scheduler failed to start", e);
        }
    }

    public static Scheduler getScheduler() {
        if (scheduler == null) {
            initiateScheduler();
        }
        return scheduler;
    }

    public static void shutdownScheduler() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            LOGGER.error("Scheduler failed to shutdown", e);
        }
    }

    public static Scheduler renewScheduler() {
        initiateScheduler();
        return scheduler;
    }
}
