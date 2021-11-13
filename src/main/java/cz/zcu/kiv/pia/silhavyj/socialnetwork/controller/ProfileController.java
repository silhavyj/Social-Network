package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IRoleService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.ProfileConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.ADMIN;

@Controller
@Validated
@RequiredArgsConstructor
public class ProfileController {

    private final IUserService userService;
    private final IRoleService roleService;

    @GetMapping("/profile")
    public String getProfile(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (user.getRoles().contains(adminRole)) {
            model.addAttribute("admin", true);
        }
        return "profile";
    }

    @PostMapping("/profile/change-profile-picture")
    public String changeProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture,
                                       @CurrentSecurityContext(expression="authentication") Authentication authentication,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {

        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);

        if (userService.isValidProfilePicture(profilePicture) == false) {
            redirectAttributes.addFlashAttribute(PROFILE_ERROR_MSG_NAME, PROFILE_PICTURE_INVALID_FORMAT_ERR_MSG);
        } else {
            userService.updateProfilePicture(user, profilePicture);
            redirectAttributes.addFlashAttribute(PROFILE_SUCCESS_MSG_NAME, PROFILE_PICTURE_UPDATED_INFO_MSG);
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/update-password")
    public String updatePassword(@RequestParam("new_password") String newPassword,
                                 @RequestParam("old_password") String oldPassword,
                                 @CurrentSecurityContext(expression="authentication") Authentication authentication,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);

        if (userService.matchesUserPassword(user, oldPassword)) {
            if (userService.isSecurePassword(newPassword)) {
                userService.setUserPassword(user, newPassword);
                redirectAttributes.addFlashAttribute(PROFILE_SUCCESS_MSG_NAME, PASSWORD_UPDATED_SUCCESSFULLY_INFO_MSG);
            } else {
                redirectAttributes.addFlashAttribute(PROFILE_ERROR_MSG_NAME, PASSWORD_IS_NOT_SECURE_ENOUGH_ERR_MSG);
            }
        } else {
            redirectAttributes.addFlashAttribute(PROFILE_ERROR_MSG_NAME, OLD_PASSWORD_DOES_NOT_MATCH_ERR_MSG);
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/update-info")
    public String updatePersonalInfo(User user,
                                     @CurrentSecurityContext(expression="authentication") Authentication authentication,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {

        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();

        userService.updatePersonalInfo(user, sessionUser);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        model.addAttribute("session_user", user);

        if (!violations.isEmpty()) {
            redirectAttributes.addFlashAttribute(PROFILE_ERROR_MSG_NAME, violations.stream().findFirst().get().getMessage());
        } else {
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute(PROFILE_SUCCESS_MSG_NAME, PROFILE_INFO_UPDATED_INFO_MSG);
        }
        return "redirect:/profile";
    }
}
