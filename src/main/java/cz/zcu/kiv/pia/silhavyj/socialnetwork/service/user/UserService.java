package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password.SecurePasswordValidator;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.UserConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.ADMIN;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecurePasswordValidator securePasswordValidator;
    private final IRoleService roleService;

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
    public void updateProfilePicture(User user, MultipartFile profilePicture) {
        String fileName = StringUtils.cleanPath(profilePicture.getOriginalFilename());
        saveProfilePicture(PROFILE_IMAGES_DIRECTORY + "/", fileName, profilePicture);
        user.setProfilePicturePath(PROFILE_IMAGES_DIRECTORY + "/" + fileName);
        userRepository.save(user);
    }

    @Override
    public boolean isValidProfilePicture(MultipartFile multipartFile) {
        if (multipartFile == null)
            return false;
        String contentType = multipartFile.getContentType();
        return isSupportedContentType(contentType);
    }

    @Override
    public boolean matchesUserPassword(User user, String oldPassword) {
        return bCryptPasswordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public void setUserPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        encryptUserPassword(user);
        userRepository.save(user);
    }

    @Override
    public boolean isSecurePassword(String password) {
        return securePasswordValidator.isValid(password, null);
    }

    @Override
    public void updatePersonalInfo(User newUser, User oldUser) {
        newUser.setPassword(oldUser.getPassword());
        newUser.setId(oldUser.getId());
        newUser.setEnabled(true);
        newUser.setEmail(oldUser.getEmail());
        newUser.setRoles(oldUser.getRoles());
        newUser.setProfilePicturePath(oldUser.getProfilePicturePath());
    }

    @Override
    public List<User> searchUsers(String name, String sessionUserEmail) {
        return userRepository.searchUsers(name, sessionUserEmail);
    }

    @Override
    public void escalateToAdmin(String email) {
        User user = userRepository.findByEmail(email).get();
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        user.getRoles().add(adminRole);
        userRepository.save(user);
    }

    @Override
    public void removeAdminPrivileges(String email) {
        User user = userRepository.findByEmail(email).get();
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        user.getRoles().remove(adminRole);
        userRepository.save(user);
    }

    private void saveProfilePicture(final String directory, final String filename, MultipartFile picture) {
        Path uploadPath = Paths.get(directory);
        try {
            if (!Files.exists(uploadPath))
                Files.createDirectories(uploadPath);
        }
        catch (IOException e) {
            logger.error(CREATING_PROFILE_PIC_DIR_FAILED_ERR_MSG, e.getMessage());
        }
        try {
            InputStream inputStream = picture.getInputStream();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            logger.error(SAVING_PROFILE_PICTURE_FAILED_ERR_MSG, e.getMessage());
        }
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
