package cz.zcu.kiv.pia.silhavyj.socialnetwork.config;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Gender;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Gender.UNDEFINED;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application")
public class AppConfiguration {
    // https://www.baeldung.com/spring-boot-yaml-list

    private String senderEmail;
    private int searchNameMinLen;
    private String url;
    private List<DefaultUserConfig> users;
    private IpBanConfig ipBanConfig;
    private List<String> bannedIp;
    private int resetPasswordExpirationTime;
    private int postsToDisplayOnProfilePage;

    @Getter
    @Setter
    public static class IpBanConfig {

        private Integer maxFailures;
        private Integer time;
    }

    @Getter
    @Setter
    public static class DefaultUserConfig {

        private String firstname;
        private String lastname;
        private String password;
        private String email;
        private String role;
        private String dob;
        private Gender gender = UNDEFINED;
        private String profilePicturePath;

        public User buildUser() {
            User user = new User();

            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPassword(password);
            user.setGender(gender);
            user.setDob(LocalDate.parse(dob));
            user.setProfilePicturePath(profilePicturePath);

            user.setEnabled(true);
            return user;
        }
    }
}