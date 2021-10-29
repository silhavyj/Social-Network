package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.ITokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService implements ITokenService {

    private final ITokenRepository tokenRepository;

    @Override
    public Optional<Token> getRegistrationTokenByUserEmail(String email) {
        return tokenRepository.findRegistrationTokenByEmail(email);
    }

    @Override
    public Optional<Token> getRegistrationTokenByTokenValue(String token) {
        return tokenRepository.findRegistrationTokenByValue(token);
    }

    @Override
    public Optional<Token> getResetPasswordTokenByUserEmail(String email) {
        return tokenRepository.findResetPasswordTokenByEmail(email);
    }

    @Override
    public Optional<Token> getResetPasswordTokenByTokenValue(String token) {
        return tokenRepository.findResetPasswordTokenByValue(token);
    }

    @Override
    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }

    @Override
    public void saveToken(Token token) {
        tokenRepository.save(token);
    }
}
