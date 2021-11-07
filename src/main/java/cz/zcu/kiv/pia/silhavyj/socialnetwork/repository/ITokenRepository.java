package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT token from Token token WHERE token.user.email = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.REGISTRATION")
    Optional<Token> findRegistrationTokenByEmail(String email);

    @Query("SELECT token from Token token WHERE token.value = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.REGISTRATION")
    Optional<Token> findRegistrationTokenByValue(String value);

    @Query("SELECT token from Token token WHERE token.user.email = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.RESET_PASSWORD")
    Optional<Token> findResetPasswordTokenByEmail(String email);

    @Query("SELECT token from Token token WHERE token.value = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.RESET_PASSWORD")
    Optional<Token> findResetPasswordTokenByValue(String value);

    void deleteByValue(String value);
}
