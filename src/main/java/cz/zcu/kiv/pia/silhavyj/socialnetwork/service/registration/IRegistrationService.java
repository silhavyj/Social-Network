package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.registration;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

/***
 * Method definitions of a registration service.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public interface IRegistrationService {

    /***
     * Sends token the user is supposed to use to reset their password
     * @param email user's e-mail address
     */
    void sendTokenForResettingPassword(String email);

    /***
     * Signs-up a user - creates their account but doesn't activate it yet. The user
     * is required to activate their account by clicking on the link sent to them via e-mail.
     * @param user the new user who wants to register
     */
    void signUpUser(User user);

    /***
     * Activates user's account
     * @param tokenValue token the user was supposed to use in order to activate their account
     */
    void activateUserAccount(String tokenValue);
}
