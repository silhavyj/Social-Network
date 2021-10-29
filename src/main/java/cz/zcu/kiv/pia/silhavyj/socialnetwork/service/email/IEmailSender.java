package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.email;

import org.springframework.scheduling.annotation.Async;

public interface IEmailSender {

    @Async
    void sendEmail(String to, String subject, String content, boolean html);
}
