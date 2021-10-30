package cz.zcu.kiv.pia.silhavyj.socialnetwork.config;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Gender;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Gender.UNDEFINED;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application")
public class AppConfiguration {
    // https://www.baeldung.com/spring-boot-yaml-list

    private String senderEmail;
    private String url;
    private List<DefaultUserConfig> users;
    private IpBanConfig ipBanConfig;
    private List<String> bannedIp;

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

        public User buildUser() {
            User user = new User();

            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPassword(password);
            user.setGender(gender);
            user.setDob(LocalDate.parse(dob));

            user.setEnabled(true);

            UserRole userRole = Stream.of(UserRole.values())
                    .filter(name -> name.name().equals(role))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Invalid use role in application.yml"));

            user.setUserRole(userRole);
            return user;
        }
    }
}