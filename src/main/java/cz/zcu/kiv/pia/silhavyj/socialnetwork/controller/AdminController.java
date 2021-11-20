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

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final IUserService userService;
    private final IRoleService roleService;
    private final IFriendshipService friendshipService;

    @GetMapping("/admin")
    public String getAdminPage(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (user.getRoles().contains(adminRole)) {
            model.addAttribute("admin", true);
        }
        return "admin";
    }

    @PutMapping("/admin/{email}")
    public ResponseEntity<?> escalateUserToAdmin(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                                 @PathVariable String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();

        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (sessionUser.getRoles().contains(adminRole) == false)
            return ResponseEntity.badRequest().body(MISSING_ADMIN_PRIVILEGES_ERR_MSG);

        if (friendshipService.isFriend(email, sessionUser) == false)
            return ResponseEntity.badRequest().body(CANNOT_CHANGE_PRIVILEGES_ERR_MSG);

        User user = userService.getUserByEmail(email).get();
        if (user.getRoles().contains(adminRole))
            return ResponseEntity.badRequest().body(ADMIN_PRIVILEGES_ALREADY_ASSIGNED_ERR_MSG);

        userService.escalateToAdmin(email);
        return ResponseEntity.ok(ADMIN_PRIVILEGES_SUCCESSFULLY_ASSIGNED_INFO_MSG);
    }

    @DeleteMapping("/admin/{email}")
    public ResponseEntity<?> unsigneAdminPrivileges(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                                                    @PathVariable String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();

        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (sessionUser.getRoles().contains(adminRole) == false)
            return ResponseEntity.badRequest().body(MISSING_ADMIN_PRIVILEGES_ERR_MSG);

        if (friendshipService.isFriend(email, sessionUser) == false)
            return ResponseEntity.badRequest().body(CANNOT_CHANGE_PRIVILEGES_ERR_MSG);

        User user = userService.getUserByEmail(email).get();
        if (user.getRoles().contains(adminRole) == false)
            return ResponseEntity.badRequest().body(USER_HAS_NO_ADMIN_PRIVILEGES_ERR_MSG);

        userService.removeAdminPrivileges(email);
        return ResponseEntity.ok(ADMIN_PRIVILEGES_SUCCESSFULLY_UNSIGNED_INFO_MSG);
    }
}