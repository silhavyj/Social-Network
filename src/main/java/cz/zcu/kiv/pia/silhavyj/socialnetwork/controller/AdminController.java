package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship.IFriendshipService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IRoleService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.AdminConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.ADMIN;

/***
 * Controller used for managing HTTP requests intended to manipulate
 * with the user privileges. These end points are only accessible by Admins.
 *
 * GET /admin            - returns the admin page
 * PUT /admin/{email}    - assigns a user Admin role
 * DELETE /admin/{email} - removes Admin role from a user
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Controller
@RequiredArgsConstructor
public class AdminController {

    /*** implementation of IUserService (manipulating with users) */
    private final IUserService userService;

    /*** implementation of IRoleService (manipulating with roles) */
    private final IRoleService roleService;

    /*** implementation of IFriendshipService (manipulating with friend requests) */
    private final IFriendshipService friendshipService;

    /***
     * Returns the content of the admin page. This page is accessible only by admins.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param model instance of Model used to pass information into the final HTML template (Thymeleaf)
     * @return a HTML page (admin page)
     */
    @GetMapping("/admin")
    public String getAdminPage(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        // Get the session user (who sent the request) and add them into
        // the html page using the model passed as a parameter.
        User user = userService.getUserByEmail(authentication.getName()).get();
        model.addAttribute("session_user", user);

        // Also, add there an addition piece of information that this is an admin logged in.
        // Thymeleaf recognizes it a changes up piece of the HTML template accordingly.
        model.addAttribute("admin", true);

        // return the HTML template (admin page)
        return "admin";
    }

    /***
     * Assigns a user the Admin role.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param email the e-mail address of the user who is going to become an admin
     * @return an instance of ResponseEntity<?> which contains resulting information of the action
     */
    @PutMapping("/admin/{email}")
    public ResponseEntity<?> escalateUserToAdmin(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                                 @PathVariable String email) {
        // Get the session user from Authentication
        User sessionUser = userService.getUserByEmail(authentication.getName()).get();

        // Make sure that the user who sent the request already has the Admin role.
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (sessionUser.getRoles().contains(adminRole) == false) {
            return ResponseEntity.badRequest().body(MISSING_ADMIN_PRIVILEGES_ERR_MSG);
        }

        // Make sure that the user who sent the request and the user who is about to become an Admin are friends.
        // This also validates the existence of the user in the system.
        if (friendshipService.isFriend(email, sessionUser) == false) {
            return ResponseEntity.badRequest().body(CANNOT_CHANGE_PRIVILEGES_ERR_MSG);
        }

        // Make sure the user who is about to become an Admin doesn't already have Admin privileges.
        User user = userService.getUserByEmail(email).get();
        if (user.getRoles().contains(adminRole) == true) {
            return ResponseEntity.badRequest().body(ADMIN_PRIVILEGES_ALREADY_ASSIGNED_ERR_MSG);
        }

        // Make the user an Admin.
        userService.escalateToAdmin(email);

        // Send a response saying all went well.
        return ResponseEntity.ok(ADMIN_PRIVILEGES_SUCCESSFULLY_ASSIGNED_INFO_MSG);
    }

    /***
     * Removes the Admin role from a user.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param email the e-mail address of the user who is going to lose the Admin privileges
     * @return an instance of ResponseEntity<?> which contains resulting information of the action
     */
    @DeleteMapping("/admin/{email}")
    public ResponseEntity<?> unsigneAdminPrivileges(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                                    @PathVariable String email) {
        // Get the session user from Authentication
        User sessionUser = userService.getUserByEmail(authentication.getName()).get();

        // Make sure that the user who sent the request already has the Admin role.
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (sessionUser.getRoles().contains(adminRole) == false) {
            return ResponseEntity.badRequest().body(MISSING_ADMIN_PRIVILEGES_ERR_MSG);
        }

        // Make sure that the user who sent the request and the user who is about to be removed Admin privileges are friends.
        // This also validates the existence of the user in the system.
        if (friendshipService.isFriend(email, sessionUser) == false) {
            return ResponseEntity.badRequest().body(CANNOT_CHANGE_PRIVILEGES_ERR_MSG);
        }

        // Make sure that the user is indeed an Admin (otherwise there's nothing to remove).
        User user = userService.getUserByEmail(email).get();
        if (user.getRoles().contains(adminRole) == false) {
            return ResponseEntity.badRequest().body(USER_HAS_NO_ADMIN_PRIVILEGES_ERR_MSG);
        }

        // Remove the admin role from the user.
        userService.removeAdminPrivileges(email);

        // Send a response saying all went well.
        return ResponseEntity.ok(ADMIN_PRIVILEGES_SUCCESSFULLY_UNSIGNED_INFO_MSG);
    }
}
