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
import org.springframework.web.bind.annotation.PutMapping;
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

/***
 * Profile controller used to handle requests sent from the profile page.
 *
 * GET  /profile                 - return the HTML template corresponding to the profile page
 * POST /profile/profile-picture - updates user's profile image
 * PUT  /profile/password        - updates user's password
 * PUT  /profile/information     - updates user's profile information
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Controller
@Validated
@RequiredArgsConstructor
public class ProfileController {

    /*** implementation of IUserService (retrieving users from the database) */
    private final IUserService userService;

    /*** implementation of IRoleService (retrieving roles from the database) */
    private final IRoleService roleService;

    /***
     * Returns the HTML template corresponding to the profile page
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page
     */
    @GetMapping("/profile")
    public String getProfile(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        // Get the instance of the session user and insert it into the HTML template.
        User user = userService.getUserByEmail(authentication.getName()).get();
        model.addAttribute("session_user", user);

        // Add additional piece of information into the HTML template (whether the session user
        // is an administrator or not), so Thymeleaf can display things meant to be
        // accessible only by the administrators.
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (user.getRoles().contains(adminRole)) {
            model.addAttribute("admin", true);
        }

        // return the HTML page
        return "profile";
    }

    /***
     * Changes user's profile image. This image is supposed to be a png, jpg, or jpeg format
     * not exceeding the size of 1 MB.
     * @param profilePicture instance of MultipartFile which represents the new profile picture
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param redirectAttributes attributes used to notify the user about any error that may occur (exceeded size, wrong format, ...)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page (profile)
     */
    @PostMapping("/profile/profile-picture")
    public String changeProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture,
                                       @CurrentSecurityContext(expression="authentication") Authentication authentication,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        // Get the instance of the session user and insert it into the HTML template.
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);

        // Check the validity of the file uploaded onto the server.
        // If it doesn't meet the requirements (format, size), an error message will be returned.
        if (userService.isValidProfilePicture(profilePicture) == false) {
            redirectAttributes.addFlashAttribute(PROFILE_ERROR_MSG_NAME, PROFILE_PICTURE_INVALID_FORMAT_ERR_MSG);
        } else {
            // Update user's profile image and return a message indicating that their
            // profile image has been updated successfully.
            userService.updateProfilePicture(user, profilePicture);
            redirectAttributes.addFlashAttribute(PROFILE_SUCCESS_MSG_NAME, PROFILE_PICTURE_UPDATED_INFO_MSG);
        }
        return "redirect:/profile";
    }

    /***
     * Updates user's password. The user is required to enter their old password
     * as well as the new password which needs to meet all requirements (not a week password).
     * @param newPassword new user's password
     * @param oldPassword old user's password (needs to match the one stored in the database)
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param redirectAttributes attributes used to notify the user about any error that may occur (old passwords do not match up, ...)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page (profile)
     */
    @PutMapping("/profile/password")
    public String updatePassword(@RequestParam("new_password") String newPassword,
                                 @RequestParam("old_password") String oldPassword,
                                 @CurrentSecurityContext(expression="authentication") Authentication authentication,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        // Get the instance of the session user and insert it into the HTML template.
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);

        // Make sure the old passwords match up, if they don't,
        // return an error message and do not proceed any further.
        if (userService.matchesUserPassword(user, oldPassword)) {
            // Make sure the new password is secure enough (entropy)
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

    /***
     * Updates user profile information.
     * @param user instance of User holding new values of the user profile (firstname, lastname, ...)
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param redirectAttributes attributes used to notify the user about any error that may occur (empty names, invalid date of birth, etc.)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page (profile)
     */
    @PutMapping("/profile/information")
    public String updatePersonalInfo(User user,
                                     @CurrentSecurityContext(expression="authentication") Authentication authentication,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        // Get the instance of the session user.
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();

        // Copy the rest of the data from the session user to the user
        // passed as a parameter, so we can check the validity.
        userService.updatePersonalInfo(user, sessionUser);

        // Make sure all attributes are valid, so we can store it into the database.
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Insert the session user into the HTML template.
        model.addAttribute("session_user", user);

        // If there is any error, return an error message and do NOT update the profile information.
        if (!violations.isEmpty()) {
            redirectAttributes.addFlashAttribute(PROFILE_ERROR_MSG_NAME, violations.stream().findFirst().get().getMessage());
        } else {
            // If the validation was successful, update the record in the database.
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute(PROFILE_SUCCESS_MSG_NAME, PROFILE_INFO_UPDATED_INFO_MSG);
        }
        return "redirect:/profile";
    }
}
