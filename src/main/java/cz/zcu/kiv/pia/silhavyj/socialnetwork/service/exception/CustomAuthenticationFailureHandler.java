package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;

@Service
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String DEFAULT_ERR_URL = "/sign-in?error=";

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException, ServletException {

        String errorMsg = INVALID_CREDENTIALS_ERR_MSG;

        if (exception.getMessage().equalsIgnoreCase(LOCKED_ACCOUNT_FLAG)) {
            errorMsg = LOCKED_ACCOUNT_ERR_MSG;
        }

        setDefaultFailureUrl(DEFAULT_ERR_URL + errorMsg);
        super.onAuthenticationFailure(request, response, exception);
    }
}