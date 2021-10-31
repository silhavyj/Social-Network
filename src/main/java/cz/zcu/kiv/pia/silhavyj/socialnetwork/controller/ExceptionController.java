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

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    private final IUserService userService;

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
    public String handleFileSizeLimitExceeded(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                              RedirectAttributes redirectAttributes,
                                              Model model) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);
        redirectAttributes.addFlashAttribute(PROFILE_ERROR_MSG_NAME, EXCEEDED_SIZE_OF_PROFILE_PICTURE);
        return "redirect:/profile";
    }
}
