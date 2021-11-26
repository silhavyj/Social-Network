package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.email;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/***
 * This class is used as a helper when sending e-mails. It loads up HTML templates
 * and inserts there appropriate data. When the content of an e-mail is created,
 * it will use the IEmailSender to finalize the process of sending off the e-mail.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@RequiredArgsConstructor
public class EmailSenderHelper implements IEmailSenderHelper {

    /*** implementation of IEmailSender (sends e-mails to the client) */
    private final IEmailSender emailSender;

    /*** instance of AppConfiguration (getting the URL) */
    private final AppConfiguration appConfiguration;

    /*** instance of SpringTemplateEngine (loading HTML templates - e-mails) */
    private final SpringTemplateEngine templateEngine;

    /***
     * Creates and sends an HTML e-mail, so the user can finish up their registration
     * and activate their account.
     * @param user instance of User who is being registered in the system
     * @param token instance of Token that has been issued for the user in order to finish the registration
     */
    @Override
    public void sendSingUpConfirmationEmail(User user, String token) {
        // Subject of the e-mail.
        String subject = "Activate your account!";

        // Data to be inserted into the HTML template (user's name and the token value - link)
        Map<String, Object> variables = Stream.of(new String[][] {
            { "firstname", user.getFirstname() },
            { "link", createLink(token, "/sign-up") },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        // Inserts the data into the context and load up the HTML e-mail template.
        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process("email/sign-up-email-confirmation", context);

        // Send the e-mail to the user.
        emailSender.sendEmail(user.getEmail(), subject, html, true);
    }

    /***
     * Sends a 'thank you for signing up' e-mail to the user.
     * @param user instance of User who has just activated their account
     */
    @Override
    public void sendThankYouForRegisteringEmail(User user) {
        // Subject of the e-mail.
        String subject = "Your account is now activated";

        // Data to be inserted into the HTML template (user's name)
        Map<String, Object> variables = Stream.of(new String[][] {
            { "firstname", user.getFirstname() }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        // Inserts the data into the context and load up the HTML e-mail template.
        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process("email/account-activated", context);

        // Send the e-mail to the user.
        emailSender.sendEmail(user.getEmail(), subject, html, true);
    }

    /***
     * Sends off an e-mail, so the user can confirm their intention to reset their password.
     * The e-mail contains a link that they're supposed to click on.
     * @param user an instance of User who wants to reset their password
     * @param token instance of Token that has been issued for the user in order to reset their password
     */
    @Override
    public void sendPasswordResetConfirmationEmail(User user, String token) {
        // Subject of the e-mail.
        String subject = "Reset the password";

        // Data to be inserted into the HTML template (user's name and the token value - link)
        Map<String, Object> variables = Stream.of(new String[][] {
            { "firstname", user.getFirstname() },
            { "link", createLink(token, "/reset-password") },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        // Inserts the data into the context and load up the HTML e-mail template.
        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process("email/reset-password-confirmation.html", context);

        // Send the e-mail to the user.
        emailSender.sendEmail(user.getEmail(), subject, html, true);
    }

    /***
     * Creates a link the user is supposed to click on as a form of confirmation.
     * This link is supposed to get clicked on when the user wants to activate their
     * account or when they want to reset their password.
     * @param token token value associated with the user
     * @param path target end point (activating account / resetting password)
     * @return create link the user is required to click on
     */
    private String createLink(String token, String path) {
        return new StringBuilder()
                .append(appConfiguration.getUrl())
                .append(path)
                .append("/")
                .append(token)
                .toString();
    }
}