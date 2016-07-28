package edu.neu.cs5500.wizards.db.jdbi;

import edu.neu.cs5500.wizards.ServiceConfiguration;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

/**
 * Created by susannaedens on 7/26/16.
 */
public class JdbiManager {

    protected DBI jdbi = null;
    private final ServiceConfiguration configuration;
    private final Environment environment;
    private static JdbiManager instance;

    private JdbiManager(ServiceConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    private void instantiateJdbi(){
        final DBIFactory factory = new DBIFactory();
        this.jdbi = factory.build(this.environment, this.configuration.getDataSourceFactory(), "postgresql");
    }

    public DBI getJdbi() {
        if (this.jdbi == null) {
            this.instantiateJdbi();
        }
        return this.jdbi;
    }

    public static JdbiManager getInstance(ServiceConfiguration configuration, Environment environment){
        if(JdbiManager.instance == null){
            JdbiManager.instance = new JdbiManager(configuration, environment);
        }
        return JdbiManager.instance;
    }

    public static JdbiManager getInstance(){
        return JdbiManager.instance;
    }

}
