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

/***
 * Controller used for managing friend requests, blocking users, or deleting friends.
 *
 * GET    /people                  - returns people page
 * GET    /people/name             - returns a list of searched users (min 3 letters need to be provided)
 * POST   /friends/{email}         - sends a friend request to another user
 * PUT    /friends/{email}         - accepts a friend request received from another user
 * DELETE /friends/{email}         - deletes a friend
 * PUT    /friends/blocked/{email} - blocks a user (after they have sent them a friend request)
 * DELETE /friends/blocked/{email} - unblocks a user
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Controller
@RequiredArgsConstructor
public class FriendshipController {

    /*** implementation of IUserService (finding users in the database) */
    private final IUserService userService;

    /*** implementation of IFriendshipService (managing friend requests) */
    private final IFriendshipService friendshipService;

    /*** implementation of IRoleService (retrieving user roles) */
    private final IRoleService roleService;

    /*** instance of AppConfiguration (custom application configuration) */
    private final AppConfiguration appConfiguration;

    /***
     * Returns the people page. This page is accessible by anyone who has registered into the system.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param model instance of Model through which data will be inserted into the finale HTML template returned to the frontend
     * @return an HTML page
     */
    @GetMapping("/people")
    public String getFriendshipManagementPage(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        // Get the session user and insert them into the final HTML template.
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", sessionUser);

        // Also, add the list of blocked, accepted, and pending friends into the HTML template.
        // The user will see these lists right away when they go onto the page.
        model.addAttribute("received_pending_requests", friendshipService.getAllReceivedPendingFriends(sessionUser));
        model.addAttribute("sent_pending_requests", friendshipService.getAllSentPendingFriends(sessionUser));
        model.addAttribute("accepted_friends_not_admins", friendshipService.getAllAcceptedFriendsNotAdmins(sessionUser));
        model.addAttribute("accepted_friends_admins", friendshipService.getAllAcceptedFriendsAdmins(sessionUser));
        model.addAttribute("blocked_friends", friendshipService.getAllBlockedFriends(sessionUser));

        // If the user is an administrator, let the HTML page know as well. Thymeleaf will
        // add some addition pieces of code that should be visible only to the administrator.
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();
        if (sessionUser.getRoles().contains(adminRole)) {
            model.addAttribute("admin", true);
        }
        // Return the HTML page.
        return "people";
    }

    /***
     * Returns a list of users matching the name (letters) provided from the frontend. At least
     * 3 letters must be provided when searching users. Blocked and accepted friends are excluded from the list.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param name name (letters) of the user we want to search in the database
     * @return a list of searched users matching the letters provided from the frontend.
     */
    @GetMapping("/people/{name}")
    public ResponseEntity<?> searchAllUsers(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                            @PathVariable("name") String name) {

        // Make sure that the user has provided enough information to search users (min 3 letters required).
        if (name == null || name.length() < appConfiguration.getSearchNameMinLen()) {
            return ResponseEntity.badRequest().body(TOO_SHORT_NAME_TO_SEARCH_ERR_MSG);
        }

        // Get the session user (who sent the request).
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();

        // Return a list of all searchable people by the session user based on the letters provided from the frontend.
        var users = friendshipService.getAllPeople(name, sessionUser);
        return ResponseEntity.ok().body(users);
    }

    /***
     * Sends a friend request to a user.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param email e-mail address of the user we want to send a friend request to
     * @return a simple plain-text message describing the result of the action
     */
    @PostMapping("/friends/{email}")
    public ResponseEntity<?> sendFriendRequest(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                               @PathVariable ("email") String email) {
        // Load up the session user.
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();

        // Make sure that the user who will be sent a friend request indeed exists in the database.
        Optional<User> receiver = userService.getUserByEmail(email);
        if (receiver.isEmpty()) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);
        }

        // Make sure that the receiving user has not been blocked by the sending user.
        if (friendshipService.isFriendshipBlocked(email, sessionUser.getEmail())) {
            return ResponseEntity.badRequest().body(USER_IS_BLOCKED_ERR_MSG);
        }

        // Make sure there is not a pending or accepted friend request
        if (friendshipService.isAlreadyFriendOrPendingFriend(email, sessionUser)) {
            return ResponseEntity.badRequest().body(FRIEND_REQUEST_ALREADY_SENT);
        }

        // Send a friend request to the user and send a response saying that everything went well.
        friendshipService.sendFriendRequest(receiver.get(), sessionUser);
        return ResponseEntity.ok(FRIEND_REQUEST_SENT_SUCCESSFULLY_INFO_MSG);
    }

    /***
     * Accepts a pending friend request.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param email e-mail address of the user who sent the friend request to the session user
     * @return a simple plain-text message describing the result of the action
     */
    @PutMapping("/friends/{email}")
    public ResponseEntity<?> acceptPendingFriendRequest(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                                        @PathVariable ("email") String email) {
        // Load up the session user.
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();

        // Make sure that the user who sent the friend request exists within the system.
        Optional<User> sender = userService.getUserByEmail(email);
        if (sender.isEmpty()) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);
        }

        // If the two users are already friend, return an error message.
        if (friendshipService.isFriend(email, sessionUser) == true) {
            return ResponseEntity.badRequest().body(FRIEND_REQUEST_ALREADY_SENT_ERR_MSG);
        }

        // Make sure that that is a pending request to be accepted.
        if (friendshipService.isPendingFriend(email, sessionUser) == false) {
            return ResponseEntity.badRequest().body(PENDING_FRIEND_REQUEST_NOT_FOUND_ERR_MSG);
        }

        // Accept the friend request and return a message saying that all went well.
        friendshipService.acceptFriendRequest(sessionUser, sender.get());
        return ResponseEntity.ok(FRIEND_REQUEST_ACCEPTED_INFO_MSG);
    }

    /***
     * Deletes a friend.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param email e-mail address of the user who is going to be removed from the session user's friend list
     * @return a simple plain-text message describing the result of the action
     */
    @DeleteMapping("/friends/{email}")
    public ResponseEntity<?> deleteFriend(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                          @PathVariable ("email") String email) {
        // Load up the session user.
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();

        // Make sure that the user who is going to be removed indeed exists in the database.
        Optional<User> sender = userService.getUserByEmail(email);
        if (sender.isEmpty()) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);
        }

        // Make sure that the two users are already friends (there is an accepted friend request).
        if (friendshipService.isAlreadyFriendOrPendingFriend(email, sessionUser) == false) {
            return ResponseEntity.badRequest().body(NO_REQUEST_TO_BE_DELETED_FOUND_ERR_MSG);
        }

        // Delete the user as a friend and return a message saying that all went well.
        friendshipService.deleteFriend(sessionUser, sender.get());
        return ResponseEntity.ok(FRIEND_SUCCESSFULLY_DELETER_INFO_MSG);
    }

    /***
     * Blocks the user who sent a friend request.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param email e-mail address of the friend who is going to be deleted
     * @return a simple plain-text message describing the result of the action
     */
    @PutMapping("/friends/blocked/{email}")
    public ResponseEntity<?> blockUser(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                       @PathVariable ("email") String email) {
        // Load up the session user.
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();

        // Make sure that the user who is going to be blocked by the session user exists in the database.
        Optional<User> sender = userService.getUserByEmail(email);
        if (sender.isEmpty()) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);
        }

        // Get the pending friend request to be used to block the user.
        // The session user is the received of the friend request - they block the user.
        Optional<FriendRequest> friendRequest = friendshipService.getFriendRequestToBlock(email, sessionUser);
        if (friendRequest.isEmpty()) {
            return ResponseEntity.badRequest().body(NO_PENDING_REQUEST_TO_BE_BLOCKED_FOUND_ERR_MSG);
        }

        // Block the user and return a message saying that all went well.
        friendshipService.blockUser(friendRequest.get());
        return ResponseEntity.ok(USER_SUCCESSFULLY_BLOCKED_INFO_MSG);
    }

    /***
     * Unblocks a user.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param email e-mail address of the blocked user (who is going be unblocked)
     * @return a simple plain-text message describing the result of the action
     */
    @DeleteMapping("/friends/blocked/{email}")
    public ResponseEntity<?> unblockUser(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                         @PathVariable ("email") String email) {
        // Load up the session user.
        String sessionUserEmail = authentication.getName();
        User sessionUser = userService.getUserByEmail(sessionUserEmail).get();

        // Make sure that the user who is going to be unblocked exists in the database.
        Optional<User> sender = userService.getUserByEmail(email);
        if (sender.isEmpty()) {
            return ResponseEntity.badRequest().body(USER_NOT_FOUND_ERR_MSG);
        }

        // Get the original friend request that has been blocked.
        // The session user is the received of the friend request.
        Optional<FriendRequest> friendRequest = friendshipService.getBLockedFriendship(sender.get().getEmail(), sessionUserEmail);
        if (friendRequest.isEmpty()) {
            return ResponseEntity.badRequest().body(FRIENDSHIP_CANNOT_BE_UNBLOCKED_ERR_MSG);
        }

        // Unblock the user and send a message saying that all went well.
        friendshipService.deleteFriend(sessionUser, sender.get());
        return ResponseEntity.ok(FRIENDSHIP_SUCCESSFULLY_UNBLOCKED);
    }
}
