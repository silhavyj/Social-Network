package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
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
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.FriendshipConstants.*;

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
        model.addAttribute("receiver_pending_requests", friendshipService.getAllUsersReceivedPendingFriends(email));
        model.addAttribute("sent_pending_requests", friendshipService.getAllUsersSentPendingFriends(email));
        model.addAttribute("friends", friendshipService.getAllUsersFriends(email));
        return "friends";
    }

    @GetMapping("/friends/search-all/{name}")
    public ResponseEntity<?> searchAllUsers(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                            @PathVariable ("name") String name) {
        if (name == null || name.length() < appConfiguration.getSearchNameMinLen())
            return ResponseEntity.badRequest().body(TOO_SHORT_NAME_TO_SEARCH_ERR_MSG);

        String email = authentication.getName();
        var users = friendshipService.getAllSearchedUser(name, email);
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/friends/add-friend/{email}")
    public ResponseEntity<?> sendFriendRequest(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                               @PathVariable ("email") String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();
        Optional<User> receiver = userService.getUserByEmail(email);

        if (receiver.isEmpty())
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);

        if (friendshipService.isAlreadyFriendOrPendingFriend(email, sessionUserEmail))
            return ResponseEntity.badRequest().body(FRIEND_REQUEST_ALREADY_SENT);

        friendshipService.sendFriendRequest(receiver.get(), sessionUser);
        return ResponseEntity.ok(FRIEND_REQUEST_SENT_SUCCESSFULLY_INFO_MSG);
    }

    @PostMapping
    public ResponseEntity<?> acceptPendingFriendRequest(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                                        @PathVariable ("email") String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();
        Optional<User> sender = userService.getUserByEmail(email);

        if (sender.isEmpty())
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);

        if (friendshipService.isFriend(email, sessionUserEmail) == true)
            return ResponseEntity.badRequest().body("You are already friends with this person");

        if (friendshipService.isPendingFriend(email, sessionUserEmail) == false)
            return ResponseEntity.badRequest().body("There is not any friend request to be accepted");

        friendshipService.acceptFriendRequest(sessionUser, sender.get());
        return ResponseEntity.ok("Friend request has been successfully accepted");
    }
}