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

    /**
     * Constructs and sends an email to the winner of an auction. Includes information on the final price of
     * the item that was won.
     *
     * @param winner the winner of the auction
     * @param item   the item that was won
     */
    public void notifyWinner(User winner, Item item) {
        if (item == null || winner == null) {
            return;
        }
        String subject = "You have won the auction for - " + item.getItemName() + "!";
        Body body = Body.builder()
                .h3("You have successfully won " + item.getItemName() + " on WizardsBay!")
                .p("You were the highest bidder for this auction.")
                .br()
                .h4(item.getItemName())
                .p("Final price: $" + item.getMinBidAmount())
                .p("Item description" + item.getItemDescription())
                .br()
                .p("Please sign into WizardsBay and confirm your purchase as soon as possible.")
                .br()
                .p("Thank you for your service, ")
                .p("The WizardsBay Team")
                .build();
        this.send(winner.getEmail(), subject, body);
    }


    /**
     * Constructs and sends an email to the loser of an auction. Includes information on the final price of the
     * item that was won and information on the item itself.
     *
     * @param bidder the bidder that has lost the item
     * @param item   the item that was being auctioned
     */
    public void notifyLoser(User bidder, Item item) {
        if (item == null || bidder == null) {
            return;
        }
        String subject = "The auction for " + item.getItemName() + " has ended";
        Body body = Body.builder()
                .h3("Sorry, you have not won the auction this time.")
                .br()
                .h4(item.getItemName())
                .p("Final price: $" + item.getMinBidAmount())
                .p("Item description: " + item.getItemDescription())
                .br()
                .p("Continue bidding on items and better luck next time!")
                .p("Thank you for your service, ")
                .p("The WizardsBay Team")
                .build();
        this.send(bidder.getEmail(), subject, body);
    }


    /**
     * Constructs and sends an email to the seller of an item after the item has been posted to WizardsBay. It includes
     * information of the item that has been posted.
     *
     * @param user the user posting the item
     * @param item the item that has been listed
     */
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
                .p("Starting price: $" + item.getMinBidAmount())
                .p("Ending time of the auction: " + item.getAuctionEndTime().toString())
                .p("Item description: " + item.getItemDescription())
                .br()
                .p("Thank you for your service, ")
                .p("The WizardsBay Team")
                .build();
        this.send(user.getEmail(), subject, body);
    }


    /**
     * Constructs and sends an email to the seller of an item after the auction has ended. It includes information
     * on the final price of the item and the winning bidder.
     *
     * @param user the seller of the item
     * @param item the item whose auction has ended
     */
    public void notifySeller(User user, Item item) {
        if (item == null || user == null) {
            return;
        }
        String subject = "The auction for " + item.getItemName() + " has ended";
        Body body;
        if (item.getSellerUsername() == null) {
            body = Body.builder()
                    .h3("The auction on your item has ended.")
                    .p("Sorry, there were no bids made on your item. ")
                    .br()
                    .h4(item.getItemName())
                    .p("Starting price: $" + item.getMinBidAmount())
                    .p("Item description: " + item.getItemDescription())
                    .br()
                    .h4("You may re-list the item on WizardsBay as soon as you like.")
                    .p("Thank you for your service, ")
                    .p("The WizardsBay Team")
                    .build();

        } else {
            body = Body.builder()
                    .h3("Congratulations on your sale of: " + item.getItemName())
                    .p("This is a confirmation email for the successful sale of the following item:")
                    .br()
                    .h4(item.getItemName())
                    .p("Final price: $" + item.getMinBidAmount())
                    .p("Buyer username: " + item.getSellerUsername())
                    .p("Item description: " + item.getItemDescription())
                    .br()
                    .p("You should be receiving your payment soon.")
                    .p("Thank you for your service, ")
                    .p("The WizardsBay Team")
                    .build();
        }
        this.send(user.getEmail(), subject, body);
    }


    /**
     * Given the email address of the recipient, the subject and the body of the email, builds and sends an email.
     *
     * @param toAddress the email address of the recipient
     * @param subject   the subject of the email
     * @param body      the body of the email
     * @return true if the email has been sent successfully
     */
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
