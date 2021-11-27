package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

import java.util.Optional;

/***
 * Method definitions of a token service.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public interface ITokenService {

    /***
     * Returns a registration token by its value
     * @param token the value of the token
     * @return registration token by its value
     */
    Optional<Token> getRegistrationTokenByTokenValue(String token);

    /***
     * Returns rest password token by the user's e-mail address
     * @param email user's e-mail address
     * @return rest password token by the user's e-mail address
     */
    Optional<Token> getResetPasswordTokenByUserEmail(String email);

    /***
     * Returns the user associated with a reset password token recognized by its token value
     * @param tokenValue token value
     * @return user associated with the reset password token
     */
    Optional<User> getGetUserByResetPasswordTokenValue(String tokenValue);

    /***
     * Deletes a token from the database
     * @param token instance of Token to be deleted from the database
     */
    void deleteToken(Token token);

    /***
     * Stores a token into the database
     * @param token instance of Token to be stored in the database
     */
    void saveToken(Token token);

    /***
     * Returns true/false depending on whether a token is expired or not
     * @param tokenValue the value of the token (used to retrieve the token from the database)
     * @return true/false depending on whether a token is expired or not
     */
    boolean isTokenExpired(String tokenValue);

    /***
     * Deletes a token from the database by its token value
     * @param token token value used to find and delete a particular token from the database
     */
    void deleteTokenByTokenValue(String token);
}
