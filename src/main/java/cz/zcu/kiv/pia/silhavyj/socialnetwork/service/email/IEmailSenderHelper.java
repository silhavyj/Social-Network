package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.email;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

/***
 * Method definitions of an e-mail sender helper.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public interface IEmailSenderHelper {

    /***
     * Creates and sends an HTML e-mail, so the user can finish up their registration
     * and activate their account.
     * @param user instance of User who is being registered in the system
     * @param token instance of Token that has been issued for the user in order to finish the registration
     */
    void sendSingUpConfirmationEmail(User user, String token);

    /***
     * Sends a 'thank you for signing up' e-mail to the user.
     * @param user instance of User who has just activated their account
     */
    void sendThankYouForRegisteringEmail(User user);

    /***
     * Sends off an e-mail, so the user can confirm their intention to reset their password.
     * The e-mail contains a link that they're supposed to click on.
     * @param user an instance of User who wants to reset their password
     * @param token instance of Token that has been issued for the user in order to reset their password
     */
    void sendPasswordResetConfirmationEmail(User user, String token);
}