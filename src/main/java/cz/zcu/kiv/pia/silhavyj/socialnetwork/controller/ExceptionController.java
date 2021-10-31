package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.exception.ResetPasswordException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.exception.SignUpException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.ProfileConstants.PROFILE_ERROR_MSG_NAME;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = {ResetPasswordException.class})
    public String resetPasswordEmailNotFoundException(ResetPasswordException exception, Model model) {
        model.addAttribute(RESET_PASSWORD_ERR_MSG_NAME, exception.getMessage());
        return "reset-password";
    }

    @ExceptionHandler(value = {SignUpException.class})
    public String userNotFoundError(SignUpException signUpRuntimeException, Model model) {
        model.addAttribute(new User());
        model.addAttribute(SIGN_UP_ERROR_MSG_NAME, signUpRuntimeException.getMessage());
        return "sign-up";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileSizeLimitExceeded(MaxUploadSizeExceededException exception, Model model) {
        model.addAttribute(PROFILE_ERROR_MSG_NAME, "File's too big");
        return "profile";
    }
}
