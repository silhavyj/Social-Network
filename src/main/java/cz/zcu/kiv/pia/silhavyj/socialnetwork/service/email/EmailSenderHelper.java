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

@Service
@RequiredArgsConstructor
public class EmailSenderHelper implements IEmailSenderHelper {

    private final IEmailSender emailSender;
    private final AppConfiguration appConfiguration;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendSingUpConfirmationEmail(User user, String token) {
        String subject = "Activate your account!";
        
        Map<String, Object> variables = Stream.of(new String[][] {
                { "firstname", user.getFirstname() },
                { "link", createLink(token, "/sign-up") },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        
        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process("email/sign-up-email-confirmation", context);
        emailSender.sendEmail(user.getEmail(), subject, html, true);
    }

    @Override
    public void sendThankYouForRegisteringEmail(User user) {
        String subject = "Your account is now activated";

        Map<String, Object> variables = Stream.of(new String[][] {
                { "firstname", user.getFirstname() }
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process("email/account-activated", context);
        emailSender.sendEmail(user.getEmail(), subject, html, true);
    }

    @Override
    public void sendPasswordResetConfirmationEmail(User user, String token) {
        String subject = "Reset the password";

        Map<String, Object> variables = Stream.of(new String[][] {
                { "firstname", user.getFirstname() },
                { "link", createLink(token, "/reset-password") },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process("email/reset-password-confirmation.html", context);
        emailSender.sendEmail(user.getEmail(), subject, html, true);
    }

    private String createLink(String token, String path) {
        return new StringBuilder()
                .append(appConfiguration.getUrl())
                .append(path)
                .append("/")
                .append(token)
                .toString();
    }
}