package edu.neu.cs5500.wizards.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WbaySchedulerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WbaySchedulerFactory.class);
    private static WbaySchedulerFactory instance = null;
    private Scheduler scheduler = null;

    private WbaySchedulerFactory() {
    }

    public static WbaySchedulerFactory getInstance() {
        if (WbaySchedulerFactory.instance == null) {
            WbaySchedulerFactory.instance = new WbaySchedulerFactory();
        }

        return WbaySchedulerFactory.instance;
    }

    private void initiateScheduler() throws SchedulerException {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            this.scheduler = sf.getScheduler();
            this.scheduler.start();
            LOGGER.info("Scheduler started");
        } catch (SchedulerException e) {
            LOGGER.error("Scheduler failed to start", e);
            throw e;
        }
    }

    public Scheduler getScheduler() throws SchedulerException {
        if (this.scheduler == null) {
            this.initiateScheduler();
        }
        return this.scheduler;
    }

    public void shutdownScheduler() throws SchedulerException {
        try {
            this.scheduler.shutdown();
            LOGGER.info("Scheduler shutdown");
        } catch (SchedulerException e) {
            LOGGER.error("Scheduler failed to shutdown", e);
            throw e;
        }
    }

    public Scheduler renewScheduler() throws SchedulerException {
        this.initiateScheduler();
        return this.scheduler;
    }
}
