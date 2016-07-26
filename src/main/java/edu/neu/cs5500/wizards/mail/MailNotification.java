package edu.neu.cs5500.wizards.mail;

import edu.neu.cs5500.wizards.core.Bid;
import edu.neu.cs5500.wizards.core.Item;
import edu.neu.cs5500.wizards.core.User;
import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.content.Body;

/**
 * Created by amala on 24/07/16.
 */
public class MailNotification {
    private static final String MAILGUN_DOMAIN = "wizardsbay.tixter.us";
    private static final String MAILGUN_API_KEY = "key-e9832d2be5768b87bd6cc56fad6ab3e5";
    private static final String MAILGUN_LOGIN = "no-reply@wizardsbay.tixter.us";

    private static Configuration configuration = new Configuration()
            .domain(MAILGUN_DOMAIN)
            .apiKey(MAILGUN_API_KEY)
            .from("WizardsBay No-Reply", MAILGUN_LOGIN);

    public void notifySuccessfulPurchase(User user, Item item, Bid bid) {
        // TODO: craft a nice little email for the user for winning the bid :D
    }

    public void notifyItemListed(User user, Item item) {
        if (item.getItemName() == null || item.getItemDescription() == null) {
            // these fields are null in testing
            return;
        }

        String subject = "Thank you for your listing at WizardsBay - " + item.getItemName();
        Body body = Body.builder()
                .h3("Item " + item.getItemName() + " has been successfully listed at WizardsBay")
                .p("This is a confirmation email from WizardsBay for your following listing:")
                .br()
                .h4(item.getItemName())
                .p(item.getItemDescription())
                .build();
        send(user.getEmail(), subject, body);
    }

    public boolean send(String toAddress, String subject, Body body) {
        return Mail.using(configuration)
                .to(toAddress)
                .subject(subject)
                .content(body)
                .build()
                .send()
                .isOk();
    }
}
