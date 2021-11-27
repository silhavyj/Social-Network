package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;

/***
 * Custom authentication failure handler. This class is used to catch any authentication
 * error that may occur and return an appropriate response as to what has gone wrong.
 * For example, when the user enters invalid credentials, or when they try to log in
 * while their account has not been activated yet.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /*** default parameter sent back to the client when an error occurs */
    private static final String DEFAULT_ERR_URL = "/sign-in?error=";

    /***
     * This method gets triggered when a user fails to authenticate.
     * @param request HTTP request sent from the client to the server
     * @param response HTTP response sent from the server back to the client
     * @param exception instance of AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException, ServletException {

        // Set the default error message - invalid credentials.
        String errorMsg = INVALID_CREDENTIALS_ERR_MSG;

        // If there is another error message, sent that one to the user first.
        if (exception.getMessage().equalsIgnoreCase(LOCKED_ACCOUNT_FLAG)) {
            errorMsg = LOCKED_ACCOUNT_ERR_MSG;
        }

        // Set the error URL and return it back to the client
        setDefaultFailureUrl(DEFAULT_ERR_URL + errorMsg);
        super.onAuthenticationFailure(request, response, exception);
    }
}