package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.registration;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.exception.ResetPasswordException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.exception.SignUpException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.email.IEmailSenderHelper;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token.ITokenService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IRoleService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.REGISTRATION;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.RESET_PASSWORD;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.USER;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

/***
 * This class handles all the logic related to registration. For instance,
 * creating a user account, enabling user's account, sending a token for
 * resetting their password, etc.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@RequiredArgsConstructor
public class RegistrationService implements IRegistrationService {

    /*** implementation of IUserService (storing/retrieving users from the database) */
    private final IUserService userService;

    /*** implementation of ITokenService (dealing with tokens) */
    private final ITokenService tokenService;

    /*** implementation of IRoleService (dealing with user roles) */
    private final IRoleService roleService;

    /*** implementation of IEmailSenderHelper (sending e-mails) */
    private final IEmailSenderHelper emailSenderHelper;

    /*** instance of AppConfiguration (token expiration times) */
    private final AppConfiguration appConfiguration;

    /***
     * Sends token the user is supposed to use to reset their password
     * @param email user's e-mail address
     */
    @Override
    public void sendTokenForResettingPassword(String email) {
        // Retrieve the user from the database (by their e-mail address).
        User user = userService.getUserByEmail(email).orElseThrow(() ->
                new ResetPasswordException(RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG));

        // Check if there's still a previous reset password token associated with this user.
        Optional<Token> previousToken = tokenService.getResetPasswordTokenByUserEmail(email);
        if (previousToken.isPresent()) {
            // If there is, check if it has expired yet and if it has,
            // just delete it from the database.
            if (previousToken.get().getExpiresAt().isBefore(now())) {
                tokenService.deleteToken(previousToken.get());
            } else {
                // If it hasn't, the user needs to wait until it expires.
                long remainingSecond = SECONDS.between(now(), previousToken.get().getExpiresAt());
                throw new ResetPasswordException(String.format(RESET_PASSWORD_TOKEN_ALREADY_ISSUED_ERR_MSG, remainingSecond));
            }
        }

        // The user account also needs to be enabled.
        if (user.isEnabled() == false) {
            throw new ResetPasswordException(ACCOUNT_HAS_NOT_BEEN_ACTIVATED_ERR_MSG);
        }

        // Issue a new reset password token with an expiration time of 5 minutes
        // and store it into the database.
        Token token = new Token(user, appConfiguration.getResetPasswordExpirationTime(), RESET_PASSWORD);
        tokenService.saveToken(token);
        emailSenderHelper.sendPasswordResetConfirmationEmail(user, token.getValue());
    }

    /***
     * Signs-up a user - creates their account but doesn't activate it yet. The user
     * is required to activate their account by clicking on the link sent to them via e-mail.
     * @param user the new user who wants to register
     */
    @Override
    public void signUpUser(User user) {
        // Make sure the user doesn't want to use an e-mail which is already taken by another user.
        userService.getUserByEmail(user.getEmail()).ifPresent(userWithTheSameEmail -> {
            throw new SignUpException(String.format(EMAIL_ALREADY_TAKEN_ERR_MSG));
        });

        // Assign the user a USER role.
        user.getRoles().clear();
        user.getRoles().add(roleService.getRoleByUserRole(USER).get());

        // Encrypt their password, so it's not stored as plain-text in the database.
        userService.encryptUserPassword(user);
        userService.saveUser(user);

        // Issue a token that they're supposed to use to activate their account
        // and send it to them via e-mail.
        final Token token = new Token(user, REGISTRATION);
        tokenService.saveToken(token);
        emailSenderHelper.sendSingUpConfirmationEmail(user, token.getValue());
    }

    /***
     * Activates user's account
     * @param tokenValue token the user was supposed to use in order to activate their account
     */
    @Override
    public void activateUserAccount(String tokenValue) {
        // Retrieve the token from the database.
        Token token = tokenService
                .getRegistrationTokenByTokenValue(tokenValue)
                .orElseThrow(() -> new SignUpException(INVALID_REGISTRATION_TOKEN));

        // Get the user who was issued this token and activate their account.
        User user = token.getUser();
        user.setEnabled(true);
        userService.saveUser(user);

        // Lastly, delete the unneeded token from the database and send
        // the user a 'thank you for singing-up' e-mail.
        tokenService.deleteToken(token);
        emailSenderHelper.sendThankYouForRegisteringEmail(user);
    }
}
