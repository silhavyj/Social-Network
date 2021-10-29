package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.registration;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.exceptions.ResetPasswordEmailNotFoundException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.exceptions.SignUpException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token.ITokenService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constants.RegistrationConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.REGISTRATION;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@RequiredArgsConstructor
public class RegistrationService implements IRegistrationService {

    private final IUserService userService;
    private final ITokenService tokenService;

    @Override
    public void sendTokenForResettingPassword(String email) {
        User user = userService.getUserByEmail(email).orElseThrow(() ->
                new ResetPasswordEmailNotFoundException(RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG));
    }

    @Override
    public void signUpUser(User user) {
        Optional<Token> previousToken = tokenService.getRegistrationToken(user.getEmail());
        if (previousToken.isPresent()) {
            if (previousToken.get().getExpiresAt().isBefore(now())) {
                tokenService.deleteToken(previousToken.get());
                userService.deleteUserByEmail(user.getEmail());
            } else {
                long remainingSecond = SECONDS.between(now(), previousToken.get().getExpiresAt());
                throw new SignUpException(String.format(REGISTRATION_TOKEN_ALREADY_ISSUED_ERR_MSG, remainingSecond));
            }
        }

        userService.getUserByEmail(user.getEmail()).ifPresent(userWithTheSameEmail -> {
            throw new SignUpException(String.format(EMAIL_ALREADY_TAKEN_ERR_MSG));
        });

        userService.encryptUserPassword(user);
        userService.saveUser(user);

        final Token token = new Token(user, REGISTRATION_TOKEN_MIN_VALIDATION, REGISTRATION);
        tokenService.saveToken(token);
    }

    @Override
    public void activateUserAccount(String token) {
        // TODO
    }
}
