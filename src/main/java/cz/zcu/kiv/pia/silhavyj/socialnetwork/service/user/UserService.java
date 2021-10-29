package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

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

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.EMAIL_NOT_FOUND_ERR_MSG;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.LOCKED_ACCOUNT_FLAG;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(EMAIL_NOT_FOUND_ERR_MSG));
        if (user.getLocked() == true)
            throw new RuntimeException(LOCKED_ACCOUNT_FLAG);
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
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUserByEmail(String email) {
        userRepository.deleteByEmail(email);
    }
}
