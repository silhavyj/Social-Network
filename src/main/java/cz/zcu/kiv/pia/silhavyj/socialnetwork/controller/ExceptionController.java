package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.exceptions.ResetPasswordException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.exceptions.SignUpException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constants.RegistrationConstants.*;

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
}
