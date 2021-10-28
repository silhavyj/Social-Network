package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.exceptions.ResetPasswordEmailNotFoundException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserConstants.LOCKED_ACCOUNT;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserConstants.RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("E-mail address not found"));
        if (user.getLocked() == true)
            throw new RuntimeException(LOCKED_ACCOUNT);
        return user;
    }

    @Override
    public void encryptUserPassword(User user) {
        final String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void sendTokenForResettingPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResetPasswordEmailNotFoundException(RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
