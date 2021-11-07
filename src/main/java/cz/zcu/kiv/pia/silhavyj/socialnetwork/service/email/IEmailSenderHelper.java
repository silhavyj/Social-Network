package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.email;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

public interface IEmailSenderHelper {

    void sendSingUpConfirmationEmail(User user, String token);
    void sendThankYouForRegisteringEmail(User user);
    void sendPasswordResetConfirmationEmail(User user, String token);
}
