package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password.SecurePasswordValidator;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.registration.IRegistrationService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.token.ITokenService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.ProfileConstants.PASSWORD_IS_NOT_SECURE_ENOUGH_ERR_MSG;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.ProfileConstants.PASSWORD_UPDATED_SUCCESSFULLY_INFO_MSG;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final IRegistrationService registrationService;
    private final IUserService userService;
    private final SecurePasswordValidator securePasswordValidator;
    private final ITokenService tokenService;

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

    @PostMapping("/sign-up")
    public String signUp(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors())
            return "sign-up";
        registrationService.signUpUser(user);
        redirectAttributes.addFlashAttribute(SIGN_IN_FORM_INFO_MSG_NAME, CONFIRM_EMAIL_ADDRESS_INFO_MSG);
        return "redirect:/sign-in";
    }

    @GetMapping("/sign-up/confirm")
    public String confirmRegistration(@RequestParam String token, Model model) {
        registrationService.activateUserAccount(token);
        model.addAttribute(SIGN_IN_FORM_SUCCESS_MSG_NAME, ACCOUNT_HAS_BEEN_ACTIVATED_INFO_MSG);
        return "sign-in";
    }

    @PostMapping("/reset-password")
    public String forgotPassword(@Valid @Email String email, RedirectAttributes redirectAttributes) {
        registrationService.sendTokenForResettingPassword(email);
        redirectAttributes.addFlashAttribute(SIGN_IN_FORM_INFO_MSG_NAME, CONFIRM_CHANGE_PASSWORD_INFO_MSG);
        return "redirect:/sign-in";
    }

    @GetMapping("/reset-password/confirm")
    public String forgotPassword(@RequestParam String token, Model model) {
        if (tokenService.isTokenExpired(token)) {
            model.addAttribute(SIGN_IN_FORM_ERR_MSG_NAME, RESET_PASSWORD_TOKEN_ALREADY_EXPIRED_ERR_MSG);
            return "sign-in";
        }
        model.addAttribute("token", token);
        return "create-new-password";
    }

    @PostMapping("/reset-password/{token}")
    public String resetPassword(@PathVariable String token, @RequestParam String password1, @RequestParam String password2, Model model) {
        Optional<User> user = tokenService.getGetUserByResetPasswordTokenValue(token);
        if (user.isEmpty()) {
            model.addAttribute(SIGN_IN_FORM_ERR_MSG_NAME, RESET_PASSWORD_TOKEN_ALREADY_EXPIRED_ERR_MSG);
            return "sign-in";
        }
        if (password1.equals(password2) == false) {
            model.addAttribute(RESET_PASSWORD_ERR_MSG_NAME, PASSWORDS_ARE_NOT_THE_SAME_ERR_MSG);
            return "create-new-password";
        } else if (securePasswordValidator.isValid(password1, null) == false) {
            model.addAttribute(RESET_PASSWORD_ERR_MSG_NAME, PASSWORD_IS_NOT_SECURE_ENOUGH_ERR_MSG);
            return "create-new-password";
        }
        userService.setUserPassword(user.get(), password1);
        tokenService.deleteTokenByTokenValue(token);
        model.addAttribute(SIGN_IN_FORM_SUCCESS_MSG_NAME, PASSWORD_UPDATED_SUCCESSFULLY_INFO_MSG);
        return "sign-in";
    }

    @GetMapping("/taken-emails/{email}")
    public ResponseEntity<?> getExistingEmailAddress(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok("taken");
        }
        return ResponseEntity.ok("free");
    }
}
