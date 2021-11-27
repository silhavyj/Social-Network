package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/***
 * Method definitions of a user service.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public interface IUserService {

    /***
     * Encrypts user's password using BCryptPasswordEncoder
     * @param user instance of User (whose password is going to be encrypted)
     */
    void encryptUserPassword(User user);

    /***
     * Saves a user to the database
     * @param user instance of User
     */
    void saveUser(User user);

    /***
     * Returns a user found by their e-mail address
     * @param email user's e-mail address
     * @return instance of User
     */
    Optional<User> getUserByEmail(String email);

    /***
     * Updates user's profile picture
     * @param user instance of User (whose profile picture is going be updated)
     * @param profilePicture instance of MultipartFile (new profile image uploaded onto the server)
     */
    void updateProfilePicture(User user, MultipartFile profilePicture);

    /***
     * Validates a file uploaded onto the server as a profile picture.
     * The image is supposed to be either .png, .jpg, or .jpeg not
     * exceeding the size of 1 MB.
     * @param multipartFile instance of MultipartFile (file uploaded onto the server)
     * @return true/false whether the file satisfies the conditions or not
     */
    boolean isValidProfilePicture(MultipartFile multipartFile);

    /***
     * Checks if the old user password, which they were supposed to enter when changing a password,
     * matched the one stored in the database. In order to compare the passwords, we need to
     * use BCryptPasswordEncoder to encrypt the plain-text password first.
     * @param user instance of User (who is changing their password)
     * @param oldPassword user's old password (plain-text)
     * @return true/false depending on whether the old password matches the one stored in the database
     */
    boolean matchesUserPassword(User user, String oldPassword);

    /***
     * Sets a new user's password. Before it gets stored into the database though,
     * it will be first encrypted using BCryptPasswordEncoder.
     * @param user instance of User
     * @param newPassword new user's password (plain-text)
     */
    void setUserPassword(User user, String newPassword);

    /***
     * Returns true/false depending on whether the password passed as a parameter is secure enough
     * @param password password (plain-text)
     * @return true/false depending on whether the password passed as a parameter is secure enough
     */
    boolean isSecurePassword(String password);

    /***
     * Copies old data over to the new user (that same one).
     * This method is used when the user wants to update their personal information.
     * They cannot change all data, so the data they cannot change is simply copied over.
     * @param newUser instance of User (user with updated personal information)
     * @param oldUser instance of User holding user's old personal information
     */
    void updatePersonalInfo(User newUser, User oldUser);

    /***
     * Assigns a user admin privileges.
     * @param email e-mail address of the user who's about to become an admin.
     */
    void escalateToAdmin(String email);

    /***
     * Removes admin privileges from a user
     * @param email e-mail address of the user who's about to lose admin privileges.
     */
    void removeAdminPrivileges(String email);

    /***
     * Returns a list of all admins existing in the database
     * @return list of all admins stored in the database
     */
    List<User> getAdmins();
}
