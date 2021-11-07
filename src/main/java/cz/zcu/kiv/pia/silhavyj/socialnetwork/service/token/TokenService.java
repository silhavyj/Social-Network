package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.ITokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static java.time.LocalDateTime.now;

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
    public Optional<User> getGetUserByResetPasswordTokenValue(String tokenValue) {
        Optional<Token> token = tokenRepository.findResetPasswordTokenByValue(tokenValue);
        if (isTokenExpired(token))
            return Optional.empty();
        return Optional.of(token.get().getUser());
    }

    @Override
    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }

    @Override
    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    @Override
    public boolean isTokenExpired(String tokenValue) {
        Optional<Token> token = tokenRepository.findResetPasswordTokenByValue(tokenValue);
        return isTokenExpired(token);
    }

    @Override
    public void deleteTokenByTokenValue(String token) {
        tokenRepository.deleteByValue(token);
    }

    boolean isTokenExpired(Optional<Token> token) {
        if (token.isEmpty())
            return true;
        if (token.get().getExpiresAt().isBefore(now())) {
            deleteToken(token.get());
            return true;
        }
        return false;
    }
}
