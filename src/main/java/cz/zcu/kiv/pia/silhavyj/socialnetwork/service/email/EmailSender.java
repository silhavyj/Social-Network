package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.email;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/***
 * This class is used as an E-mail sender. An e-mail is sent off when the user
 * is required to change finish up their registration or when they want to change their password.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@RequiredArgsConstructor
public class EmailSender implements IEmailSender {

    /*** logger used when something goes wrong when sending an e-mail */
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    /*** instance of AppConfiguration (sender e-mail address) */
    private final AppConfiguration appConfiguration;

    /*** instance of JavaMailSender (sends e-mails to the client) */
    private final JavaMailSender javaMailSender;

    /***
     * Sends an e-mail to the user
     * @param to e-mail address of the receiver
     * @param subject subject of the e-mail
     * @param content content of the e-mail (HTML e-mail, or plain-text)
     * @param html flag if the content is an HTML format
     */
    @Override
    public void sendEmail(String to, String subject, String content, boolean html) {
        try {
            // Create a mime message template.
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

            // Set up all the necessary information about the e-mail.
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, html);
            mimeMessage.setFrom(appConfiguration.getSenderEmail());

            // Send the e-mail to the client.
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // Log the error in case something goes wrong.
            logger.error(String.format("failed to send email of subject '%s' to '%s'", subject, to), e.getMessage());
        }
    }
}
