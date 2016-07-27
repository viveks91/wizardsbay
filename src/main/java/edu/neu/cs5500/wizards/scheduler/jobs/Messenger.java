package edu.neu.cs5500.wizards.scheduler.jobs;

import edu.neu.cs5500.wizards.core.Bid;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import edu.neu.cs5500.wizards.db.BidDAO;
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

import java.util.ArrayList;
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
        final BidDAO bidDao = jdbiInstance.onDemand(BidDAO.class);
        final UserDAO userDao = jdbiInstance.onDemand(UserDAO.class);
        final ItemDAO itemDao = jdbiInstance.onDemand(ItemDAO.class);

        // retrieve the item and the list of bids for that item
        Item item = itemDao.findItemById(itemId);
        List<Bid> bidList = bidDao.findBidsByItemId(itemId);

        List<Integer> userIds = new ArrayList<>();
        for (Bid bid : bidList) {
            userIds.add(bid.getBidderId());
        }

        //> notify all users that bid on the item whether they have won or lost the auction
        List<User> users = userDao.RetrieveUsersByIds(userIds);
        MailService mailService = new MailService();
        for (User user : users) {
            if (user.getId().equals(item.getBuyerId())) {
                mailService.notifyWinner(user, item);
                LOGGER.info("Winner: " + user.getUsername());
            } else {
                mailService.notifyLoser(user, item);
                LOGGER.info("Bidder: " + user.getUsername());
            }
        }

        // notify the seller of the auction that the auction is over
        User seller = userDao.retrieve(item.getSellerUsername());
        mailService.notifySeller(seller, item);

    }
}
