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

@Service
@RequiredArgsConstructor
public class RegistrationService implements IRegistrationService {

    private final IUserService userService;
    private final ITokenService tokenService;
    private final IRoleService roleService;
    private final IEmailSenderHelper emailSenderHelper;
    private final AppConfiguration appConfiguration;

    @Override
    public void sendTokenForResettingPassword(String email) {
        User user = userService.getUserByEmail(email).orElseThrow(() ->
                new ResetPasswordException(RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG));

        Optional<Token> previousToken = tokenService.getResetPasswordTokenByUserEmail(email);
        if (previousToken.isPresent()) {
            if (previousToken.get().getExpiresAt().isBefore(now())) {
                tokenService.deleteToken(previousToken.get());
            } else {
                long remainingSecond = SECONDS.between(now(), previousToken.get().getExpiresAt());
                throw new ResetPasswordException(String.format(RESET_PASSWORD_TOKEN_ALREADY_ISSUED_ERR_MSG, remainingSecond));
            }
        }
        if (user.isEnabled() == false)
            throw new ResetPasswordException(ACCOUNT_HAS_NOT_BEEN_ACTIVATED_ERR_MSG);

        Token token = new Token(user, appConfiguration.getResetPasswordExpirationTime(), RESET_PASSWORD);
        tokenService.saveToken(token);
        emailSenderHelper.sendPasswordResetConfirmationEmail(user, token.getValue());
    }

    @Override
    public void signUpUser(User user) {
        userService.getUserByEmail(user.getEmail()).ifPresent(userWithTheSameEmail -> {
            throw new SignUpException(String.format(EMAIL_ALREADY_TAKEN_ERR_MSG));
        });

        user.getRoles().clear();
        user.getRoles().add(roleService.getRoleByUserRole(USER).get());
        userService.encryptUserPassword(user);
        userService.saveUser(user);

        final Token token = new Token(user, REGISTRATION);
        tokenService.saveToken(token);
        emailSenderHelper.sendSingUpConfirmationEmail(user, token.getValue());
    }

    @Override
    public void activateUserAccount(String tokenValue) {
        Token token = tokenService
                .getRegistrationTokenByTokenValue(tokenValue)
                .orElseThrow(() -> new SignUpException(INVALID_REGISTRATION_TOKEN));

        User user = token.getUser();
        user.setEnabled(true);
        userService.saveUser(user);

        tokenService.deleteToken(token);
        emailSenderHelper.sendThankYouForRegisteringEmail(user);
    }
}
