package cz.zcu.kiv.pia.silhavyj.socialnetwork.config;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.exception.CustomAuthenticationFailureHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/sign-up/**", "/password-entropy/**", "/sign-in/**", "/reset-password/**", "/css/*", "/js/*", "/pictures/*").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/sign-in")
            .defaultSuccessUrl("/", true)
            .failureHandler(authenticationFailureHandler())
            .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userService)
            .passwordEncoder(bCryptPasswordEncoder);
    }
}