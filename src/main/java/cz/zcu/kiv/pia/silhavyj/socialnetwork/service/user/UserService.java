package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.UserConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.ADMIN;

/***
 * This class handles all the logic related to users. For instance,
 * retrieving users from the database, encrypting their passwords,
 * updating their profile image, etc.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {

    /*** logger used to log an error if saving user's profile image fails */
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /*** implementation of IUserRepository (managing users in the database) */
    private final IUserRepository userRepository;

    /*** instance of BCryptPasswordEncoder (encrypting users' passwords) */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /*** instance of SecurePasswordValidator (assessing the strength of a user's password) */
    private final SecurePasswordValidator securePasswordValidator;

    /*** implementation of IRoleService (retrieving user roles from the database) */
    private final IRoleService roleService;

    /***
     * Returns an instance of UserDetails (User) found in the database by their username (password).
     * This method is used by Spring Security during the process of authentication.
     * @param email username (user's e-mail address)
     * @return instance of UserDetails (user)
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(EMAIL_NOT_FOUND_ERR_MSG));
        if (user.getLocked() == true) {
            throw new RuntimeException(LOCKED_ACCOUNT_FLAG);
        }
        return user;
    }

    /***
     * Encrypts user's password using BCryptPasswordEncoder
     * @param user instance of User (whose password is going to be encrypted)
     */
    @Override
    public void encryptUserPassword(User user) {
        final String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
    }

    /***
     * Saves a user to the database
     * @param user instance of User
     */
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /***
     * Returns a user found by their e-mail address
     * @param email user's e-mail address
     * @return instance of User
     */
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /***
     * Updates user's profile picture
     * @param user instance of User (whose profile picture is going be updated)
     * @param profilePicture instance of MultipartFile (new profile image uploaded onto the server)
     */
    @Override
    public void updateProfilePicture(User user, MultipartFile profilePicture) {
        String fileName = StringUtils.cleanPath(profilePicture.getOriginalFilename());
        saveProfilePicture(PROFILE_IMAGES_DIRECTORY + "/", fileName, profilePicture);
        user.setProfilePicturePath(PROFILE_IMAGES_DIRECTORY + "/" + fileName);
        userRepository.save(user);
    }

    /***
     * Validates a file uploaded onto the server as a profile picture.
     * The image is supposed to be either .png, .jpg, or .jpeg not
     * exceeding the size of 1 MB.
     * @param multipartFile instance of MultipartFile (file uploaded onto the server)
     * @return true/false whether the file satisfies the conditions or not
     */
    @Override
    public boolean isValidProfilePicture(MultipartFile multipartFile) {
        if (multipartFile == null) {
            return false;
        }
        String contentType = multipartFile.getContentType();
        return isSupportedContentType(contentType);
    }

    /***
     * Checks if the old user password, which they were supposed to enter when changing a password,
     * matched the one stored in the database. In order to compare the passwords, we need to
     * use BCryptPasswordEncoder to encrypt the plain-text password first.
     * @param user instance of User (who is changing their password)
     * @param oldPassword user's old password (plain-text)
     * @return true/false depending on whether the old password matches the one stored in the database
     */
    @Override
    public boolean matchesUserPassword(User user, String oldPassword) {
        return bCryptPasswordEncoder.matches(oldPassword, user.getPassword());
    }

    /***
     * Sets a new user's password. Before it gets stored into the database though,
     * it will be first encrypted using BCryptPasswordEncoder.
     * @param user instance of User
     * @param newPassword new user's password (plain-text)
     */
    @Override
    public void setUserPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        encryptUserPassword(user);
        userRepository.save(user);
    }

    /***
     * Returns true/false depending on whether the password passed as a parameter is secure enough
     * @param password password (plain-text)
     * @return true/false depending on whether the password passed as a parameter is secure enough
     */
    @Override
    public boolean isSecurePassword(String password) {
        return securePasswordValidator.isValid(password, null);
    }

    /***
     * Copies old data over to the new user (that same one).
     * This method is used when the user wants to update their personal information.
     * They cannot change all data, so the data they cannot change is simply copied over.
     * @param newUser instance of User (user with updated personal information)
     * @param oldUser instance of User holding user's old personal information
     */
    @Override
    public void updatePersonalInfo(User newUser, User oldUser) {
        newUser.setPassword(oldUser.getPassword());
        newUser.setId(oldUser.getId());
        newUser.setEnabled(true);
        newUser.setEmail(oldUser.getEmail());
        newUser.setRoles(oldUser.getRoles());
        newUser.setProfilePicturePath(oldUser.getProfilePicturePath());
    }

    /***
     * Assigns a user admin privileges.
     * @param email e-mail address of the user who's about to become an admin.
     */
    @Override
    public void escalateToAdmin(String email) {
        User user = userRepository.findByEmail(email).get();
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        user.getRoles().add(adminRole);
        userRepository.save(user);
    }

    /***
     * Removes admin privileges from a user
     * @param email e-mail address of the user who's about to lose admin privileges.
     */
    @Override
    public void removeAdminPrivileges(String email) {
        User user = userRepository.findByEmail(email).get();
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        user.getRoles().remove(adminRole);
        userRepository.save(user);
    }

    /***
     * Returns a list of all admins existing in the database
     * @return list of all admins stored in the database
     */
    @Override
    public List<User> getAdmins() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(roleService.getRoleByUserRole(ADMIN).get());
        return userRepository.findByRolesIn(roles);
    }

    /***
     * Saves the profile picture onto the server
     * @param directory path to the directory where all profile images are stored
     * @param filename name of the file which represents the user's profile image
     * @param picture the profile picture itself
     */
    private void saveProfilePicture(final String directory, final String filename, MultipartFile picture) {
        // If the directory doesn't exist, create it first.
        Path uploadPath = Paths.get(directory);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        }
        catch (IOException e) {
            logger.error(CREATING_PROFILE_PIC_DIR_FAILED_ERR_MSG, e.getMessage());
        }

        // Store the image into that directory.
        try {
            InputStream inputStream = picture.getInputStream();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            logger.error(SAVING_PROFILE_PICTURE_FAILED_ERR_MSG, e.getMessage());
        }
    }

    /***
     * Checks if the content type corresponds to .png, .jpg, or .jpeg
     * @param contentType file type
     * @return true, if the type falls into the types listed above, false otherwise
     */
    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
