package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/***
 * JPA repository that handles SQL queries regarding tokens.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Repository
public interface ITokenRepository extends JpaRepository<Token, Long> {

    /***
     * Returns a registration token found by user's e-mail address
     * @param email e-mail address of the user associated with the token
     * @return matching token (Optional - can be null)
     */
    @Query("SELECT token from Token token WHERE token.user.email = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.REGISTRATION")
    Optional<Token> findRegistrationTokenByEmail(String email);

    /***
     * Returns a registration token found by token value
     * @param value the token value
     * @return matching token (Optional - can be null)
     */
    @Query("SELECT token from Token token WHERE token.value = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.REGISTRATION")
    Optional<Token> findRegistrationTokenByValue(String value);

    /***
     * Returns a reset password token found by user's e-mail address
     * @param email e-mail address of the user associated with the token
     * @return matching token (Optional - can be null)
     */
    @Query("SELECT token from Token token WHERE token.user.email = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.RESET_PASSWORD")
    Optional<Token> findResetPasswordTokenByEmail(String email);

    /***
     * Returns a reset password token found by token value
     * @param value the token value
     * @return matching token (Optional - can be null)
     */
    @Query("SELECT token from Token token WHERE token.value = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.RESET_PASSWORD")
    Optional<Token> findResetPasswordTokenByValue(String value);

    /***
     * Deletes a token from the database by its value
     * @param value the value of the token
     */
    void deleteByValue(String value);
}
