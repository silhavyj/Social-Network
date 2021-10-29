package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;

import java.util.Optional;

public interface ITokenService {

    Optional<Token> getRegistrationToken(String email);
    void deleteToken(Token token);
    void saveToken(Token token);
}
