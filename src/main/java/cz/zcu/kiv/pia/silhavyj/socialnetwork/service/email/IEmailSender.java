package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.email;

import org.springframework.scheduling.annotation.Async;

/***
 * Method definitions of an e-mail sender.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public interface IEmailSender {

    /***
     * Send an e-mail to the user.
     * @param to e-mail address of the receiver
     * @param subject e-mail subject
     * @param content e-mail content
     * @param html flag if the content is an HTML format
     */
    @Async
    void sendEmail(String to, String subject, String content, boolean html);
}
