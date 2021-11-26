package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.chat;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat.OnlinePeopleStorage;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat.Message;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship.IFriendshipService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat.MessageType.USER_ONLINE;

/***
 * Even handler when the client connects the web socket (chat). This event
 * is used as an indication of a user being now online. When this happens, we need to add
 * them to the data structure that keeps track of who is currently online. Also, we
 * need to let all their online friends know that this person is now available for chat.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@RequiredArgsConstructor
public class StompSubscribedEvent implements ApplicationListener<SessionSubscribeEvent> {

    /*** collection of currently online users */
    private final OnlinePeopleStorage onlinePeopleStorage;

    /*** implementation of IFriendshipService (retrieving user's friends from the database) */
    private final IFriendshipService friendshipService;

    /*** implementation of IUserService (retrieving the session user) */
    private final IUserService userService;

    /*** instance of MessageSendingOperations (sending messages to particular end points using web sockets) */
    private final MessageSendingOperations<String> simpMessagingTemplate;

    /***
     * Detects when a user gets online (they connect to the web socket - chat)
     * @param event instance of SessionSubscribeEvent
     */
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        // Get the session user e-mail (who just went online)
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userEmail = stompHeaderAccessor.getUser().getName();

        // Add them to the data structure that holds currently online people
        onlinePeopleStorage.getOnlinePeople().add(userEmail);

        // Load the user from the database and retrieve all their friends.
        User user = userService.getUserByEmail(userEmail).get();
        var friends = friendshipService.getAllAcceptedFriends(userEmail);

        // Let all their online friends know of this person being now online. Also, we need to notify the
        // user who just went online of all their friends who are also online and therefore available for chat
        for (var friend : friends) {
            simpMessagingTemplate.convertAndSend("/topic/messages/" + friend.getEmail(), new Message(user.getFullName(), user.getEmail(), user.getProfilePicturePath(), USER_ONLINE));
            if (onlinePeopleStorage.getOnlinePeople().contains(friend.getEmail())) {
                simpMessagingTemplate.convertAndSend("/topic/messages/" + userEmail, new Message(friend.getFullName(), friend.getEmail(), friend.getProfilePicturePath(), USER_ONLINE));
            }
        }
    }
}
