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
import org.springframework.web.bind.annotation.GetMapping;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.ADMIN;

/***
 * Controller used to return the main home page. This is where the user
 * gets redirected after successfully signing in.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Controller
@RequiredArgsConstructor
public class HomepageController {

    /*** implementation of IUserService (loads users from the database) */
    private final IUserService userService;

    /*** implementation of IRoleService (loads user roles from the database) */
    private final IRoleService roleService;

    /***
     * Returns the main home HTML page.
     * @param authentication an instance of Authentication through which we get the session user
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page
     */
    @GetMapping("/")
    public String homePage(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        // Add the session user into the HTML template
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);

        // Also, if the session user is an administration let Thymeleaf know, so it can add
        // some additional pieces of code that should be visible only to the administrators.
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (user.getRoles().contains(adminRole)) {
            model.addAttribute("admin", true);
        }

        // Return the home page.
        return "home-page";
    }
}

