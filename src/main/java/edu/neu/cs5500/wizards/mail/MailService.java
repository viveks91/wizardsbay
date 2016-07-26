package edu.neu.cs5500.wizards.mail;

import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.content.Body;

/**
 * Created by amala on 24/07/16.
 */
public class MailService {
    private static final String MAILGUN_DOMAIN = "wizardsbay.tixter.us";
    private static final String MAILGUN_API_KEY = "key-e9832d2be5768b87bd6cc56fad6ab3e5";
    private static final String MAILGUN_LOGIN = "no-reply@wizardsbay.tixter.us";

    private static Configuration configuration = new Configuration()
            .domain(MAILGUN_DOMAIN)
            .apiKey(MAILGUN_API_KEY)
            .from("WizardsBay No-Reply", MAILGUN_LOGIN);

    public void notifyWinner(User winner, Item item) {
        // TODO: craft a nice little email for the winning bidder :D Winning price is the minBidAMount
    }

    public void notifyBidders(User bidder, Item item) {
        // TODO: craft a nice little email for the other bidders
    }

    public void notifyItemListed(User user, Item item) {
        if (item == null || user == null) {
            return;
        }

        String subject = "Thank you for your listing on WizardsBay - " + item.getItemName();
        Body body = Body.builder()
                        .h3("Item " + item.getItemName() + " has been successfully listed on WizardsBay")
                        .p("This is a confirmation email for your posting the following item:")
                        .br()
                        .h4(item.getItemName())
                        .p(item.getItemDescription())
                        .build();
        this.send(user.getEmail(), subject, body);
    }

    private boolean send(String toAddress, String subject, Body body) {
        return Mail.using(configuration)
                   .to(toAddress)
                   .subject(subject)
                   .content(body)
                   .build()
                   .send()
                   .isOk();
    }
}
