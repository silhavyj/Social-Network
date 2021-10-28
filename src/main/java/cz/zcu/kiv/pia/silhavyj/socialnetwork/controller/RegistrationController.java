package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserConstants.SIGN_IN_FORM_MSG_NAME;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final IUserService userService;

    @GetMapping("/sign-in")
    public String signIn() {
        return "sign-in";
    }

    @GetMapping("/reset-password")
    public String forgotPassword() {
        return "reset-password";
    }

    @GetMapping("/sign-up")
    public String signUp(User user) {
        return "sign-up";
    }

    @GetMapping("/logout")
    public String logout() {
        return "sign-in";
    }

    @PostMapping("/reset-password")
    public String forgotPassword(@Valid @Email String email, RedirectAttributes redirectAttributes) {
        userService.sendTokenForResettingPassword(email);
        redirectAttributes.addFlashAttribute(SIGN_IN_FORM_MSG_NAME, "Please confirm your intention to change the password through e-mail");
        return "redirect:/sign-in";
    }
}
