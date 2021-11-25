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

@Service
@RequiredArgsConstructor
public class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {

    private final OnlinePeopleStorage onlinePeopleStorage;
    private final IFriendshipService friendshipService;
    private final MessageSendingOperations<String> simpMessagingTemplate;
    private final IUserService userService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userEmail = stompHeaderAccessor.getUser().getName();
        onlinePeopleStorage.getOnlinePeople().remove(userEmail);
        User user = userService.getUserByEmail(userEmail).get();

        var friends = friendshipService.getAllAcceptedFriends(userEmail);
        for (var friend : friends) {
            simpMessagingTemplate.convertAndSend("/topic/messages/" + friend.getEmail(), new Message(user.getFullName(), user.getEmail(), user.getProfilePicturePath(), USER_OFFLINE));
            if (onlinePeopleStorage.getOnlinePeople().contains(friend.getEmail())) {
                simpMessagingTemplate.convertAndSend("/topic/messages/" + userEmail, new Message(friend.getFullName(), friend.getEmail(), friend.getProfilePicturePath(), USER_OFFLINE));
            }
        }
    }
}
