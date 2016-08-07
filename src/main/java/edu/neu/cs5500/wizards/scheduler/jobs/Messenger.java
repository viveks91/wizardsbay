package edu.neu.cs5500.wizards.scheduler.jobs;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.ItemDAO;
import edu.neu.cs5500.wizards.db.UserDAO;
import edu.neu.cs5500.wizards.db.jdbi.JdbiManager;
import edu.neu.cs5500.wizards.mail.MailService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Messenger implements Job {

    public Messenger() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Messenger.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        int itemId = data.getIntValue("itemId");

        // get jdbi
        JdbiManager jdbiManager = JdbiManager.getInstance();
        final DBI jdbiInstance = jdbiManager.getJdbi();

        // get dao instances
        final ItemDAO itemDao = jdbiInstance.onDemand(ItemDAO.class);
        final UserDAO userDAO = jdbiInstance.onDemand(UserDAO.class);

        // retrieve the item and the list of participants
        Item item = itemDao.findItemById(itemId);
        List<User> users = userDAO.getParticipantsByItemId(itemId);

        // Notify participants
        MailService mailService = new MailService();
        for (User user : users) {
            if (user.getId().equals(item.getBuyerId())) {
                mailService.notifyWinner(user, item);
            } else {
                mailService.notifyLoser(user, item);
            }
        }

        // notify the seller of the auction that the auction is over
        User seller = userDAO.retrieveById(item.getSellerId());

        User buyer = null;
        if(item.getBuyerId() != null){
            buyer = userDAO.retrieveById(item.getBuyerId());
        }
        mailService.notifySeller(seller, item, buyer);

        LOGGER.info("Item: " + item.getItemName() + " auction has ended. The seller has been notified.");
    }
}
