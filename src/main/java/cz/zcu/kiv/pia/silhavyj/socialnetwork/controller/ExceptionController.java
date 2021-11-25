package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.exception.ResetPasswordException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.exception.SignUpException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.ProfileConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;

/***
 * Custom exception controller used to handle exceptions that occur
 * when signing up, uploading a profile image, or resetting a password.
 * When an exception is thrown, the user will be presented with a custom
 * feedback message (frontend) that will give them a clue as to what happened.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    /*** implementation of IUserService (used for loading users from the database) */
    private final IUserService userService;

    /***
     * ResetPasswordException handler. This error occurs when the user wants to reset their password
     * the second time while still having issued a first attempt token, or for example, when they try
     * to reset their password without having activated the account first.
     * @param exception instance of ResetPasswordException (thrown exception)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page containing an error message
     */
    @ExceptionHandler(value = { ResetPasswordException.class })
    public String resetPasswordEmailNotFoundException(ResetPasswordException exception, Model model) {
        model.addAttribute(RESET_PASSWORD_ERR_MSG_NAME, exception.getMessage());
        return "reset-password";
    }

    /***
     * SignUpException handler. This error occurs when the user want to sign up with an e-mail that
     * has been already taken by another user. Another example of where this error takes place is
     * when they try to activate their account with an invalid toked.
     * @param signUpRuntimeException instance of SignUpException (thrown exception)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page containing an error message
     */
    @ExceptionHandler(value = { SignUpException.class })
    public String userNotFoundError(SignUpException signUpRuntimeException, Model model) {
        model.addAttribute(new User());
        model.addAttribute(SIGN_UP_ERROR_MSG_NAME, signUpRuntimeException.getMessage());
        return "sign-up";
    }

    /***
     * MaxUploadSizeExceededException handler. This error occurs when the user tries to upload an image
     * that exceeds the maximum size limit.
     * @param authentication an instance of Authentication through which we get the session user
     * @param redirectAttributes attributes we pass on to the HTML (frontend)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page containing an error message
     */
    @ExceptionHandler(value = { MaxUploadSizeExceededException.class })
    public String handleFileSizeLimitExceeded(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                              RedirectAttributes redirectAttributes,
                                              Model model) {
        // Get the session user.
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();

        // Insert the error message into the HTML template.
        model.addAttribute("session_user", user);
        redirectAttributes.addFlashAttribute(PROFILE_ERROR_MSG_NAME, EXCEEDED_SIZE_OF_PROFILE_PICTURE);
        return "redirect:/profile";
    }
}
