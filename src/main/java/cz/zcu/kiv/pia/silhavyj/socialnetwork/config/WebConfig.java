package cz.zcu.kiv.pia.silhavyj.socialnetwork.config;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat.OnlinePeopleStorage;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.exception.CustomAuthenticationFailureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/***
 * Configuration class that creates all beans used in the project.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Configuration
public class WebConfig {

    /***
     * Creates an encoder for encoding users passwords before storing them into the databse.
     * @return an instance of BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /***
     * Creates storage (data structure) for keeping track of online users
     * @return an instance of OnlinePeopleStorage
     */
    @Bean
    public OnlinePeopleStorage onlinePeopleStorage() {
        return new OnlinePeopleStorage();
    }

    /***
     * Creates a custom authentication handler
     * @return an instance of AuthenticationFailureHandler
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}