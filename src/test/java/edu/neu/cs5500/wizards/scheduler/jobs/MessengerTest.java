package edu.neu.cs5500.wizards.scheduler.jobs;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import edu.neu.cs5500.wizards.db.jdbi.JdbiManager;
import edu.neu.cs5500.wizards.mail.MailService;
import edu.neu.cs5500.wizards.scheduler.WbaySchedulerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class, WbaySchedulerFactory.class, JdbiManager.class, Messenger.class, LoggerFactory.class})
public class MessengerTest {

    @Mock
    JobDataMap jobDataMap;

    @Mock
    ItemDAO itemDAO;

    @Mock
    UserDAO userDAO;

    @Mock
    MailService mailService;

    @Mock
    JdbiManager jdbiManager;

    @Mock
    DBI jdbi;

    @Mock
    JobExecutionContext jobExecutionContext;

    @Mock
    Item item;

    @Mock
    User winner;

    @Mock
    User loser;

    @Mock
    JobDetail jobDetail;

    @Mock
    Logger logger;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(JdbiManager.class);
        PowerMockito.mockStatic(LoggerFactory.class);

        when(jobDataMap.getIntValue(anyString())).thenReturn(new Random().nextInt());
        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
        when(jobExecutionContext.getJobDetail()).thenReturn(jobDetail);

        int winnerId = new Random().nextInt();

        when(item.getBuyerId()).thenReturn(winnerId);
        when(item.getSellerId()).thenReturn(winnerId);
        when(winner.getId()).thenReturn(winnerId);

        List<User> users = new ArrayList<>();
        users.add(winner);
        users.add(loser);

        when(itemDAO.findItemById(anyInt())).thenReturn(item);
        when(userDAO.getParticipantsByItemId(anyInt())).thenReturn(users);
        when(userDAO.retrieveById(anyInt())).thenReturn(Mockito.mock(User.class));

        when(jdbi.onDemand(ItemDAO.class)).thenReturn(itemDAO);
        when(jdbi.onDemand(UserDAO.class)).thenReturn(userDAO);
        when(jdbiManager.getJdbi()).thenReturn(jdbi);
        PowerMockito.when(JdbiManager.getInstance()).thenReturn(jdbiManager);
        PowerMockito.whenNew(MailService.class).withAnyArguments().thenReturn(mailService);
        PowerMockito.when(LoggerFactory.getLogger(any(Class.class))).thenReturn(logger);
    }

    @Test
    public void testJobExecution() throws JobExecutionException {
        Messenger messenger = new Messenger();
        messenger.execute(jobExecutionContext);
    }
}
