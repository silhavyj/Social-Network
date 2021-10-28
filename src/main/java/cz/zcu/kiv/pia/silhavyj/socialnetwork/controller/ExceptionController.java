package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.exceptions.ResetPasswordEmailNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserConstants.RESET_PASSWORD_ERR_MSG_NAME;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = {ResetPasswordEmailNotFoundException.class})
    public String resetPasswordEmailNotFoundException(ResetPasswordEmailNotFoundException exception, Model model) {
        model.addAttribute(RESET_PASSWORD_ERR_MSG_NAME, exception.getMessage());
        return "reset-password";
    }
}
