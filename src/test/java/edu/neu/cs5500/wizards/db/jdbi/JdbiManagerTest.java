package edu.neu.cs5500.wizards.db.jdbi;


import edu.neu.cs5500.wizards.ServiceConfiguration;
import edu.neu.cs5500.wizards.resources.ItemResource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skife.jdbi.v2.DBI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JdbiManager.class)
public class JdbiManagerTest {

    @Mock
    ServiceConfiguration serviceConfiguration;

    @Mock
    Environment environment;

    @Mock
    DBIFactory dbiFactory;

    @Mock
    DBI jdbi;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(dbiFactory.build(any(Environment.class), any(PooledDataSourceFactory.class), anyString())).thenReturn(jdbi);
        PowerMockito.whenNew(DBIFactory.class).withAnyArguments().thenReturn(dbiFactory);
    }

    @Test
    public void testGetInstance() {
        JdbiManager jdbiManager = JdbiManager.getInstance(serviceConfiguration, environment);
        assertEquals(JdbiManager.class, jdbiManager.getClass());
    }

    @Test
    public void testGetJdbi() {
        JdbiManager jdbiManager = JdbiManager.getInstance(serviceConfiguration, environment);
        DBI jdbi1 = jdbiManager.getJdbi();
        assertEquals(jdbi, jdbi1);
    }
}
