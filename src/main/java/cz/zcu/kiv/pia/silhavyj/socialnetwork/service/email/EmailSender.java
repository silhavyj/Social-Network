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

@Service
@RequiredArgsConstructor
public class EmailSender implements IEmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private final AppConfiguration appConfiguration;
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String content, boolean html) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, html);
            mimeMessage.setFrom(appConfiguration.getSenderEmail());

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error(String.format("failed to send email of subject '%s' to '%s'", subject, to), e.getMessage());
        }
    }
}
