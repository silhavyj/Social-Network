package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.ITokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static java.time.LocalDateTime.now;

/***
 * This class handles all the logic related to tokens. For instance,
 * retrieving tokens from the database by their value, deleting tokens,
 * checking if a token is expired, etc.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TokenService implements ITokenService {

    /*** implementation of ITokenRepository (retrieving/storing tokens from the database) */
    private final ITokenRepository tokenRepository;

    /***
     * Returns a registration token by its value
     * @param token the value of the token
     * @return registration token by its value
     */
    @Override
    public Optional<Token> getRegistrationTokenByTokenValue(String token) {
        return tokenRepository.findRegistrationTokenByValue(token);
    }

    /***
     * Returns rest password token by the user's e-mail address
     * @param email user's e-mail address
     * @return rest password token by the user's e-mail address
     */
    @Override
    public Optional<Token> getResetPasswordTokenByUserEmail(String email) {
        return tokenRepository.findResetPasswordTokenByEmail(email);
    }

    /***
     * Returns the user associated with a reset password token recognized by its token value
     * @param tokenValue token value
     * @return user associated with the reset password token
     */
    @Override
    public Optional<User> getGetUserByResetPasswordTokenValue(String tokenValue) {
        Optional<Token> token = tokenRepository.findResetPasswordTokenByValue(tokenValue);
        if (isTokenExpired(token)) {
            return Optional.empty();
        }
        return Optional.of(token.get().getUser());
    }

    /***
     * Deletes a token from the database
     * @param token instance of Token to be deleted from the database
     */
    @Override
    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }

    /***
     * Stores a token into the database
     * @param token instance of Token to be stored in the database
     */
    @Override
    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    /***
     * Returns true/false depending on whether a token is expired or not
     * @param tokenValue the value of the token (used to retrieve the token from the database)
     * @return true/false depending on whether a token is expired or not
     */
    @Override
    public boolean isTokenExpired(String tokenValue) {
        Optional<Token> token = tokenRepository.findResetPasswordTokenByValue(tokenValue);
        return isTokenExpired(token);
    }

    /***
     * Deletes a token from the database by its token value
     * @param token token value used to find and delete a particular token from the database
     */
    @Override
    public void deleteTokenByTokenValue(String token) {
        tokenRepository.deleteByValue(token);
    }

    /***
     * Returns true/false depending on whether a token is expired or not
     * @param token token which we want to know if it's expired or not
     * @return true/false depending on whether a token is expired or not
     */
    boolean isTokenExpired(Optional<Token> token) {
        if (token.isEmpty()) {
            return true;
        }
        if (token.get().getExpiresAt().isBefore(now())) {
            deleteToken(token.get());
            return true;
        }
        return false;
    }
}
