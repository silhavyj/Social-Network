package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship.FriendshipService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship.IFriendshipService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IRoleService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.FriendshipConstants.FRIENDSHIP_SUCCESSFULLY_UNBLOCKED;
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

        if (friendshipService.isFriend(email, sessionUser) == false)
            return ResponseEntity.badRequest().body("You cannot change role of someone who you are not friends with");

        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        User user = userService.getUserByEmail(email).get();
        if (user.getRoles().contains(adminRole))
            return ResponseEntity.badRequest().body("Your friend already has admin privileges");

        userService.escalateToAdmin(email);
        return ResponseEntity.ok(FRIENDSHIP_SUCCESSFULLY_UNBLOCKED);
    }
}
