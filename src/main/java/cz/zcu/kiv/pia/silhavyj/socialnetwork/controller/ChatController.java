package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat.Message;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat.OnlinePeopleStorage;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship.IFriendshipService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static java.time.LocalDateTime.now;

/***
 * Controller used for sending messages between two friends (users).
 * The chat itself is implemented using WebSockets.
 *
 * /chat/{receiver} - sends a message to receiver
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@RestController
@RequiredArgsConstructor
public class ChatController {

    /*** implementation of IUserService (checking if a user exists) */
    private final IUserService userService;

    /*** instance of SimpMessagingTemplate (sending messages to the clientd) */
    private final SimpMessagingTemplate simpMessagingTemplate;

    /*** instance of OnlinePeopleStorage (checking if a user is online) */
    private final OnlinePeopleStorage onlinePeopleStorage;

    /*** implementation of IFriendshipService (checking if sender and receiver are friends) */
    private final IFriendshipService friendshipService;

    /***
     * Sends a message over to another user. In order to send the message successfully, the sender and
     * receiver have to be friends. Furthermore, the receiver needs to be online. If any of these conditions
     * is not satisfied, the message will be ignored.
     * @param receiver e-mail address of the message receiver
     * @param message instance of Message which defines what data is being sent from sender to receiver
     * @param authentication an instance of Authentication through which we get the session user (sender)
     */
    @MessageMapping("/chat/{receiver}")
    public void sendMessage(@DestinationVariable String receiver, Message message,
                            @CurrentSecurityContext(expression="authentication") Authentication authentication) {

        // Make sure the message is not empty.
        if (message.getMessage() == null || message.getMessage().trim().length() == 0) {
            return;
        }

        // Load up the session user.
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();

        // Make sure the receiver exists within and also that they are online.
        Optional<User> toUser = userService.getUserByEmail(receiver);
        if (toUser.isEmpty() || onlinePeopleStorage.getOnlinePeople().contains(toUser.get().getEmail()) == false) {
            return;
        }

        // Make sure the sender and receiver are friends.
        if (friendshipService.isFriend(receiver, sessionUser) == false) {
            return;
        }

        // Add additional information into the message, so the receiver can for example
        // display the sender's profile picture, and so on.
        message.setSenderFullName(sessionUser.getFullName());
        message.setSenderEmail(sessionUser.getEmail());
        message.setProfilePicturePath(sessionUser.getProfilePicturePath());
        message.setTimeStamp(now().toString());

        // Forward the message over to the receiver.
        simpMessagingTemplate.convertAndSend("/topic/messages/" + receiver, message);
    }
}
