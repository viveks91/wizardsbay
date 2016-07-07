package edu.neu.cs5500.wizards;

import edu.neu.cs5500.wizards.cli.RenderCommand;
import edu.neu.cs5500.wizards.core.Template;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import edu.neu.cs5500.wizards.resources.ItemResource;
import edu.neu.cs5500.wizards.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.skife.jdbi.v2.DBI;

import java.util.Map;

public class EbayCloneApplication extends Application<EbayCloneConfiguration> {
    public static void main(String[] args) throws Exception {
        new EbayCloneApplication().run(args);
    }

//    private final HibernateBundle<EbayCloneConfiguration> hibernateBundle =
//        new HibernateBundle<EbayCloneConfiguration>(User.class) {
//            @Override
//            public DataSourceFactory getDataSourceFactory(EbayCloneConfiguration configuration) {
//                return configuration.getDataSourceFactory();
//            }
//        };

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<EbayCloneConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        //
        bootstrap.addBundle(new SwaggerBundle<EbayCloneConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(EbayCloneConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });

        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<EbayCloneConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(EbayCloneConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(new ViewBundle<EbayCloneConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(EbayCloneConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(EbayCloneConfiguration configuration, Environment environment) throws ClassNotFoundException {
        final Template template = configuration.buildTemplate();
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
        final UserDAO userDao = jdbi.onDemand(UserDAO.class);
        environment.jersey().register(new UserResource(userDao));
        final ItemDAO itemDao = jdbi.onDemand(ItemDAO.class);
        environment.jersey().register(new ItemResource(itemDao));
    }
}
