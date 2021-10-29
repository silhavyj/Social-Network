package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT token from Token token WHERE token.user.email = ?1 AND token.tokenType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.TokenType.REGISTRATION")
    Optional<Token> getRegistrationTokenByEmail(String email);
}
