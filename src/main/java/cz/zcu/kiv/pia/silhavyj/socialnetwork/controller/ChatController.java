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

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final IUserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OnlinePeopleStorage onlinePeopleStorage;
    private final IFriendshipService friendshipService;

    @MessageMapping("/chat/{receiver}")
    public void sendMessage(@DestinationVariable String receiver, Message message,
                            @CurrentSecurityContext(expression="authentication") Authentication authentication) {

        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        Optional<User> toUser = userService.getUserByEmail(receiver);

        if (toUser.isEmpty() || onlinePeopleStorage.getOnlinePeople().contains(toUser.get().getEmail()) == false)
            return;

        if (friendshipService.isFriend(receiver, sessionUser) == false)
            return;

        message.setSenderFullName(sessionUser.getFullName());
        message.setSenderEmail(sessionUser.getEmail());
        message.setProfilePicturePath(sessionUser.getProfilePicturePath());
        simpMessagingTemplate.convertAndSend("/topic/messages/" + receiver, message);
    }
}
