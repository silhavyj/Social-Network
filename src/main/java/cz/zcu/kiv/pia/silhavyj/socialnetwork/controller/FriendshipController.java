package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
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
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.FriendshipConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.ADMIN;

@Controller
@RequiredArgsConstructor
public class FriendshipController {

    private final IUserService userService;
    private final IFriendshipService friendshipService;
    private final AppConfiguration appConfiguration;
    private final IRoleService roleService;

    @GetMapping("/people")
    public String getFriendshipManagementPage(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", sessionUser);

        model.addAttribute("received_pending_requests", friendshipService.getAllReceivedPendingFriends(sessionUser));
        model.addAttribute("sent_pending_requests", friendshipService.getAllSentPendingFriends(sessionUser));
        model.addAttribute("accepted_friends", friendshipService.getAllAcceptedFriends(sessionUser));
        model.addAttribute("blocked_friends", friendshipService.getAllBlockedFriends(sessionUser));

        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (sessionUser.getRoles().contains(adminRole)) {
            model.addAttribute("admin", true);
        }
        return "people";
    }

    @GetMapping("/people/{name}")
    public ResponseEntity<?> searchAllUsers(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                            @PathVariable("name") String name) {
        if (name == null || name.length() < appConfiguration.getSearchNameMinLen())
            return ResponseEntity.badRequest().body(TOO_SHORT_NAME_TO_SEARCH_ERR_MSG);

        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        var users = friendshipService.getAllPeople(name, sessionUser);
        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/friends/{email}")
    public ResponseEntity<?> sendFriendRequest(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                               @PathVariable ("email") String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();
        Optional<User> receiver = userService.getUserByEmail(email);

        if (receiver.isEmpty())
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);

        if (friendshipService.isFriendshipBlocked(email, sessionUser.getEmail()))
            return ResponseEntity.badRequest().body(USER_IS_BLOCKED_ERR_MSG);

        if (friendshipService.isAlreadyFriendOrPendingFriend(email, sessionUser))
            return ResponseEntity.badRequest().body(FRIEND_REQUEST_ALREADY_SENT);

        friendshipService.sendFriendRequest(receiver.get(), sessionUser);
        return ResponseEntity.ok(FRIEND_REQUEST_SENT_SUCCESSFULLY_INFO_MSG);
    }

    @PutMapping("/friends/{email}")
    public ResponseEntity<?> acceptPendingFriendRequest(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                                        @PathVariable ("email") String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();
        Optional<User> sender = userService.getUserByEmail(email);

        if (sender.isEmpty())
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);

        if (friendshipService.isFriend(email, sessionUser) == true)
            return ResponseEntity.badRequest().body(FRIEND_REQUEST_ALREADY_SENT_ERR_MSG);

        if (friendshipService.isPendingFriend(email, sessionUser) == false)
            return ResponseEntity.badRequest().body(PENDING_FRIEND_REQUEST_NOT_FOUND_ERR_MSG);

        friendshipService.acceptFriendRequest(sessionUser, sender.get());
        return ResponseEntity.ok(FRIEND_REQUEST_ACCEPTED_INFO_MSG);
    }

    @DeleteMapping("/friends/{email}")
    public ResponseEntity<?> deleteFriend(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                          @PathVariable ("email") String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();
        Optional<User> sender = userService.getUserByEmail(email);

        if (sender.isEmpty())
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);

        if (friendshipService.isAlreadyFriendOrPendingFriend(email, sessionUser) == false)
            return ResponseEntity.badRequest().body(NO_REQUEST_TO_BE_DELETED_FOUND_ERR_MSG);

        friendshipService.deleteFriend(sessionUser, sender.get());
        return ResponseEntity.ok(FRIEND_SUCCESSFULLY_DELETER_INFO_MSG);
    }

    @PutMapping("/friends/blocked/{email}")
    public ResponseEntity<?> blockUser(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                       @PathVariable ("email") String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();
        Optional<User> sender = userService.getUserByEmail(email);

        if (sender.isEmpty())
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);

        Optional<FriendRequest> friendRequest = friendshipService.getFriendRequestToBlock(email, sessionUser);
        if (friendRequest.isEmpty())
            return ResponseEntity.badRequest().body(NO_PENDING_REQUEST_TO_BE_BLOCKED_FOUND_ERR_MSG);

        friendshipService.blockUser(friendRequest.get());
        return ResponseEntity.ok(USER_SUCCESSFULLY_BLOCKED_INFO_MSG);
    }

    @DeleteMapping("friends/blocked/{email}")
    public ResponseEntity<?> unblockUser(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                         @PathVariable ("email") String email) {
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();
        Optional<User> sender = userService.getUserByEmail(email);

        if (sender.isEmpty())
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);

        Optional<FriendRequest> friendRequest = friendshipService.getBLockedFriendship(sender.get().getEmail(), sessionUserEmail);
        if (friendRequest.isEmpty())
            return ResponseEntity.badRequest().body(FRIENDSHIP_CANNOT_BE_UNBLOCKED_ERR_MSG);

        friendshipService.deleteFriend(sessionUser, sender.get());
        return ResponseEntity.ok(FRIENDSHIP_SUCCESSFULLY_UNBLOCKED);
    }
}
