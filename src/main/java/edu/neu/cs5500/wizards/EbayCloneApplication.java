package edu.neu.cs5500.wizards;

import edu.neu.cs5500.wizards.auth.ServiceAuthenticator;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.BidDAO;
import edu.neu.cs5500.wizards.db.FeedbackDAO;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import edu.neu.cs5500.wizards.db.binder.IntegerListArgumentFactory;
import edu.neu.cs5500.wizards.resources.BidResource;
import edu.neu.cs5500.wizards.resources.FeedbackResource;
import edu.neu.cs5500.wizards.resources.ItemResource;
import edu.neu.cs5500.wizards.resources.UserResource;
import edu.neu.cs5500.wizards.scheduler.JobScheduler;
import edu.neu.cs5500.wizards.scheduler.jobs.TestJob;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class EbayCloneApplication extends Application<ServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new EbayCloneApplication().run(args);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EbayCloneApplication.class);

    @Override
    public String getName() {
        return "ebay-clone";
    }

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        // Swagger
        bootstrap.addBundle(new SwaggerBundle<ServiceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ServiceConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });

        // Migrations
        bootstrap.addBundle(new MigrationsBundle<ServiceConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ServiceConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(ServiceConfiguration configuration, Environment environment) throws ClassNotFoundException {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

        // Jdbi List Binder
        jdbi.registerArgumentFactory(new IntegerListArgumentFactory());

        // User endpoints
        final UserDAO userDao = jdbi.onDemand(UserDAO.class);
        final ItemDAO itemDaoForUser = jdbi.onDemand(ItemDAO.class);
        environment.jersey().register(new UserResource(userDao, itemDaoForUser));

        // Item endpoints
        final ItemDAO itemDao = jdbi.onDemand(ItemDAO.class);
        environment.jersey().register(new ItemResource(itemDao, userDao));

        // Bids endpoints
        final BidDAO bidDao = jdbi.onDemand(BidDAO.class);
        final UserDAO userDaoForBids = jdbi.onDemand(UserDAO.class);
        final ItemDAO itemDaoForBids = jdbi.onDemand(ItemDAO.class);
        environment.jersey().register(new BidResource(bidDao, userDaoForBids, itemDaoForBids));

        // Feedback endpoints
        final FeedbackDAO feedbackDao = jdbi.onDemand(FeedbackDAO.class);
        final UserDAO userDaoForFeedback = jdbi.onDemand(UserDAO.class);
        environment.jersey().register(new FeedbackResource(feedbackDao, userDaoForFeedback));

        // authentication
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new ServiceAuthenticator(userDao))
                        .setRealm("Needs Authentication")
                        .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        // Scheduler
        JobScheduler jobScheduler = JobScheduler.getInstance();
        final Scheduler scheduler = jobScheduler.getScheduler();

        // Schedule all the active items
        try {
            ItemDAO itemDAOForJobs = jdbi.onDemand(ItemDAO.class);
            List<Item> activeItems = itemDAOForJobs.findAllActiveItems();
            for (Item item : activeItems) {
                JobDetail job = newJob(TestJob.class)
                        .withIdentity("job" + item.getId(), "active")
                        .build();
                job.getJobDataMap().put("item", item.getId());

                Trigger trigger = newTrigger()
                        .withIdentity("trigger" + item.getId(), "active")
                        .startAt(item.getAuctionEndTime())
                        .build();

                scheduler.scheduleJob(job, trigger);
            }

        } catch (SchedulerException e) {
            LOGGER.error("Failed to schedule a job", e);
        }

    }

}
