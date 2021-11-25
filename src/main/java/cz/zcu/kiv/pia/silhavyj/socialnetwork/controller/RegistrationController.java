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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.ProfileConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.RegistrationConstants.*;

/***
 * Registration controller used for handling the process of registration
 * and all that is related to it - resetting password, activating account, etc.
 * All these end-points are publicly accessible.
 *
 * GET  /sign-in                - returns the sign-in page
 * GET  /logout                 - returns the sign-in page after the user successfully logs out
 * GET  /taken-emails/{email}   - return whether the e-mail is already taken or not
 *
 * GET  /reset-password         - returns the reset password page
 * GET  /reset-password/{token} - sends the user onto a page where they can reset their password
 * POST /reset-password/{token} - resets user's password
 *
 * GET  /sign-up                - returns the sign-up page
 * POST /sign-up                - registers a user (stores them into the database)
 * GET  /sign-up/{token}        - activates user's account (once they've clicked on the link sent via e-mail)
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Controller
@RequiredArgsConstructor
public class RegistrationController {

    /*** implementation of IRegistrationService (signing up user) */
    private final IRegistrationService registrationService;

    /*** implementation of IUserService (managing users) */
    private final IUserService userService;

    /*** implementation of ITokenService (issuing tokens) */
    private final ITokenService tokenService;

    /** instance of SecurePasswordValidator (assessing password strength) */
    private final SecurePasswordValidator securePasswordValidator;

    /***
     * Returns the sign-in HTML page
     * @return sign-in page
     */
    @GetMapping("/sign-in")
    public String signIn() {
        return "sign-in";
    }

    /***
     * Returns the reset password HTML page
     * @return reset password page
     */
    @GetMapping("/reset-password")
    public String forgotPassword() {
        return "reset-password";
    }

    /***
     * Returns the sign-up HTML page
     * @param user instance of User which is used as a data mode within Thymeleaf when signing up
     * @return sign-up page
     */
    @GetMapping("/sign-up")
    public String signUp(User user) {
        return "sign-up";
    }

    /***
     * Redirects the user to the sign-in page after they have successfully logged out
     * @return sign-in page
     */
    @GetMapping("/logout")
    public String logout() {
        return "sign-in";
    }

    /***
     * Signs-up a user
     * @param user User model that contains values of all the fields in the sign-up form
     * @param result instance of BindingResult used for checking the results of the validation
     * @param redirectAttributes attributes used to notify the user about any error that may occur (weak password, empty name, etc.)
     * @return sign-in page if there was no error detected, otherwise the sign-up page with an error displayed
     */
    @PostMapping("/sign-up")
    public String signUp(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes) {
        // Check if the instance of User passed in as a parameter is valid or not.
        if (result.hasErrors()) {
            return "sign-up";
        }
        // If all the fields in the sign-up form meet the requirements,
        // store the user in the database, and let them know that they're
        // required to confirm their registration via e-mail (redirectAttributes)
        registrationService.signUpUser(user);
        redirectAttributes.addFlashAttribute(SIGN_IN_FORM_INFO_MSG_NAME, CONFIRM_EMAIL_ADDRESS_INFO_MSG);
        return "redirect:/sign-in";
    }

    /***
     * Activates user account after they have clicked on the link sent to them via e-mail
     * @param token token value associated with the user (used to confirm the registration)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return feedback information displayed in the sign-in page telling the user the result of the action
     */
    @GetMapping("/sign-up/{token}")
    public String confirmRegistration(@PathVariable String token, Model model) {
        registrationService.activateUserAccount(token);
        model.addAttribute(SIGN_IN_FORM_SUCCESS_MSG_NAME, ACCOUNT_HAS_BEEN_ACTIVATED_INFO_MSG);
        return "sign-in";
    }

    /***
     * Send the user an e-mail with a link leading to a page where they can reset their password.
     * If the e-mail they have entered into the form is not found, they'll be presented with an error message.
     * @param email e-mail address of the user who wants to reset their password
     * @param redirectAttributes attributes used to notify the user about any error that may occur (e-mail address not found)
     * @return redirect to the sign-in page if the e-mail has been found within the system
     */
    @PostMapping("/reset-password")
    public String resetPassword(@Valid @Email String email, RedirectAttributes redirectAttributes) {
        registrationService.sendTokenForResettingPassword(email);
        redirectAttributes.addFlashAttribute(SIGN_IN_FORM_INFO_MSG_NAME, CONFIRM_CHANGE_PASSWORD_INFO_MSG);
        return "redirect:/sign-in";
    }

    /***
     * Returns an HTML page where the user can change their password.
     * If the token value doesn't match the issued one, the user will be sent to the sign-page
     * and presented with an error message.
     * @param token token value that has been issued to the user in order to reset their password
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page where the user can change their password
     */
    @GetMapping("/reset-password/{token}")
    public String resetPasswordConfirmation(@PathVariable String token, Model model) {
        // If the token doesn't exist or is expired, send the user to the sign-in page.
        if (tokenService.isTokenExpired(token)) {
            model.addAttribute(SIGN_IN_FORM_ERR_MSG_NAME, RESET_PASSWORD_TOKEN_ALREADY_EXPIRED_ERR_MSG);
            return "sign-in";
        }
        // Add the token value into the HTML template so the user can
        // send it back to the sever along with the new password (we know that it's them)
        model.addAttribute("token", token);
        return "create-new-password";
    }

    /***
     * Resets user's password. The user is required to enter the new password twice and send it
     * to the server along with token that they have been issued for this purpose.
     * @param token token value that the user has been issued to change reset their password
     * @param password1 new user's password
     * @param password2 confirmation of the new user's password
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return If everything goes well, the user will be sent to the sign-in page. Otherwise, they will be presented with an error message.
     */
    @PostMapping("/reset-password/{token}")
    public String resetPasswordUpdatePassword(@PathVariable String token, @RequestParam String password1, @RequestParam String password2, Model model) {
        // Find the user by their reset password token. If no user is found,
        // display an error message in the sign-in page
        Optional<User> user = tokenService.getGetUserByResetPasswordTokenValue(token);
        if (user.isEmpty()) {
            model.addAttribute(SIGN_IN_FORM_ERR_MSG_NAME, RESET_PASSWORD_TOKEN_ALREADY_EXPIRED_ERR_MSG);
            return "sign-in";
        }

        // Make sure that the two passwords are the same. Also, make sure that the new password is secure enough.
        if (password1.equals(password2) == false) {
            model.addAttribute(RESET_PASSWORD_ERR_MSG_NAME, PASSWORDS_ARE_NOT_THE_SAME_ERR_MSG);
            return "create-new-password";
        } else if (securePasswordValidator.isValid(password1, null) == false) {
            model.addAttribute(RESET_PASSWORD_ERR_MSG_NAME, PASSWORD_IS_NOT_SECURE_ENOUGH_ERR_MSG);
            return "create-new-password";
        }

        // Update user's password and delete the token from the database.
        userService.setUserPassword(user.get(), password1);
        tokenService.deleteTokenByTokenValue(token);

        // Display a message information the user that their password has been successfully reset.
        model.addAttribute(SIGN_IN_FORM_SUCCESS_MSG_NAME, PASSWORD_UPDATED_SUCCESSFULLY_INFO_MSG);

        // Return the sign-in page
        return "sign-in";
    }

    /***
     * Returns a short binary response indicating whether the e-mail passed
     * to the method is already taken by another user or not. This method is called
     * during the registration process when the user clicks off the e-mail field in the sign-up form.
     * @param email e-mail address the user wants to use
     * @return "taken" if the e-mail is already taken, "free" otherwise.
     */
    @GetMapping("/taken-emails/{email}")
    public ResponseEntity<?> getExistingEmailAddress(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(EMAIL_IS_TAKEN_KEYWORD);
        }
        return ResponseEntity.ok(EMAIL_IS_FREE_KEYWORD);
    }
}
