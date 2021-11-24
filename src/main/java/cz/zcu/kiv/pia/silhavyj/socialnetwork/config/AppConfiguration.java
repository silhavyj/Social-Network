package cz.zcu.kiv.pia.silhavyj.socialnetwork.config;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Gender;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Gender.UNDEFINED;

/***
 * Custom configuration wrapper that holds values used throughout the project.
 * It parses the 'application' section in application.yml which contains
 * values such as default users, the token expiration time when the user
 * wants to reset their password, etc.
 * Inspiration: https://www.baeldung.com/spring-boot-yaml-list
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application")
public class AppConfiguration {

    /*** email address the application uses to send emails */
    private String senderEmail;

    /*** minimal characters required when searching users */
    private int searchNameMinLen;

    /*** URL address of the application (localhost) */
    private String url;

    /*** default users that will be inserted into the database when the application starts */
    private List<DefaultUserConfig> users;

    /*** token expiration time when resetting a password */
    private int resetPasswordExpirationTime;

    /*** number of posts to be displayed on the profile page as well as the home page */
    private int postsToDisplayOnProfilePage;

    /***
     * Nested structure that holds basic information about a user to be inserted into the database.
     */
    @Getter
    @Setter
    public static class DefaultUserConfig {

        /*** user's firstname */
        private String firstname;

        /*** user's lastname */
        private String lastname;

        /*** user's password */
        private String password;

        /*** user's e-mail address */
        private String email;

        /*** user's default role */
        private String role;

        /*** user's date of birth */
        private String dob;

        /*** user's default gender */
        private Gender gender = UNDEFINED;

        /*** path to the user's profile picture */
        private String profilePicturePath;

        /***
         * Creates an instance of User out of the information listed out in application.yml
         * @return instance of User which is an expected structure to be inserted into the database
         */
        public User buildUser() {
            // Create a new instance of User
            User user = new User();

            // Set all the information about the user
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPassword(password);
            user.setGender(gender);
            user.setDob(LocalDate.parse(dob));
            user.setProfilePicturePath(profilePicturePath);

            // Since it is a default user account, it needs to be enabled
            user.setEnabled(true);

            // Return the instance of the user, so it can be stored in the database.
            return user;
        }
    }
}