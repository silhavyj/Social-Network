package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.registration.IRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constants.RegistrationConstants.*;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final IRegistrationService registrationService;

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
        registrationService.sendTokenForResettingPassword(email);
        redirectAttributes.addFlashAttribute(SIGN_IN_FORM_INFO_MSG_NAME, "Please confirm your intention to change the password through e-mail");
        return "redirect:/sign-in";
    }

    @PostMapping("/sign-up")
    public String signUp(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors())
            return "sign-up";
        registrationService.signUpUser(user);
        redirectAttributes.addFlashAttribute(SIGN_IN_FORM_INFO_MSG_NAME, "Please confirm your e-mail address to finish the registration");
        return "redirect:/sign-in";
    }

    @GetMapping("/sign-up/confirm")
    public String confirmRegistration(@RequestParam String token, Model model) {
        registrationService.activateUserAccount(token);
        model.addAttribute(SIGN_IN_FORM_SUCCESS_MSG_NAME, ACCOUNT_HAS_BEEN_ACTIVATED_INFO_MSG);
        return "sign-in";
    }
}
