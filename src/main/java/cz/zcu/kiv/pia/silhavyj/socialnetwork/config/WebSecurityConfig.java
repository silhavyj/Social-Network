package cz.zcu.kiv.pia.silhavyj.socialnetwork.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/***
 * Web security configuration. This class defines accessibility of different end-points
 * based on user roles. It also defines things like login page, custom fail authentication
 * handler or pages and resources that are public (no authentication required).
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /*** instance of UserDetailsService (required for authentication) */
    private final UserDetailsService userService;

    /*** instance of BCryptPasswordEncoder (encoding user passwords) */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /*** instance of  AuthenticationFailureHandler (custom handler) */
    private final AuthenticationFailureHandler authenticationFailureHandler;

    /***
     * Sets up web security configuration for the whole application.
     * @param http instance of HttpSecurity which is used for the configuration of web security
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(
                        "/sign-up/**",
                        "/password-entropy/**",
                        "/create-new-password/**",
                        "/sign-in/**",
                        "/taken-emails/**",
                        "/reset-password/**",
                        "/css/*",
                        "/js/*",
                        "/pictures/*").permitAll()
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/posts/announcements/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/sign-in")
                .defaultSuccessUrl("/", true)
                .failureHandler(authenticationFailureHandler)
            .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
    }

    /***
     * Configures authentication within spring boot web security. This requires
     * us to pass in an instance of UserDetailsService as well as an encoder
     * which is used to encode user password before they are sorted in the database.
     * @param auth an instance of AuthenticationManagerBuilder through which we set up the configuration
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userService)
            .passwordEncoder(bCryptPasswordEncoder);
    }
}