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
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat.MessageType.USER_OFFLINE;

/***
 * Even handler when the client disconnects from the web socket. This event
 * is used as an indication of a user going offline. When this happens, we need to remove
 * them from the data structure that keeps track of who is currently online. Also, we
 * need to let all their online friends know that this person is no longer available for chat.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@RequiredArgsConstructor
public class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {

    /*** collection of currently online users */
    private final OnlinePeopleStorage onlinePeopleStorage;

    /*** implementation of IFriendshipService (retrieving user's friends from the database) */
    private final IFriendshipService friendshipService;

    /*** implementation of IUserService (retrieving the session user) */
    private final IUserService userService;

    /*** instance of MessageSendingOperations (sending messages to particular end points using web sockets) */
    private final MessageSendingOperations<String> simpMessagingTemplate;

    /***
     * Detects when a user goes offline (they disconnect from the web socket)
     * @param event instance of SessionDisconnectEvent
     */
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        // Get the session user e-mail (who just went offline)
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userEmail = stompHeaderAccessor.getUser().getName();

        // Remove them from the data structure that holds currently online people
        onlinePeopleStorage.getOnlinePeople().remove(userEmail);

        // Load the user from the database and retrieve all their friends.
        User user = userService.getUserByEmail(userEmail).get();
        var friends = friendshipService.getAllAcceptedFriends(userEmail);

        // Let all their online friends know of this person being offline.
        for (var friend : friends) {
            simpMessagingTemplate.convertAndSend("/topic/messages/" + friend.getEmail(), new Message(user.getFullName(), user.getEmail(), user.getProfilePicturePath(), USER_OFFLINE));
        }
    }
}
