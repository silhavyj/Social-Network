package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship.IFriendshipService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FriendshipController {

    private final IUserService userService;
    private final IFriendshipService friendshipService;
    private final AppConfiguration appConfiguration;

    @GetMapping("/friends")
    public String getFriendshipManagementPage(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);
        return "friends";
    }

    @GetMapping("/friends/search-all/{name}")
    public ResponseEntity<?> searchAllUsers(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                            @PathVariable ("name") String name) {

        if (name == null || name.length() < appConfiguration.getSearchNameMinLen())
            return ResponseEntity.badRequest().build();

        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        var users = friendshipService.getAllSearchedUser(name, sessionUser.getEmail());
        return ResponseEntity.ok().body(users);
    }
}