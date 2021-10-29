package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;

import java.util.Optional;

public interface ITokenService {

    Optional<Token> getRegistrationTokenByUserEmail(String email);
    Optional<Token> getRegistrationTokenByTokenValue(String token);
    Optional<Token> getResetPasswordTokenByUserEmail(String email);
    Optional<Token> getResetPasswordTokenByTokenValue(String token);
    void deleteToken(Token token);
    void saveToken(Token token);
}
