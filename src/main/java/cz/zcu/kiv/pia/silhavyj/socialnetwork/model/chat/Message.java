package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Message {

    private String senderFullName;
    private String senderEmail;
    private String profilePicturePath;
    private MessageType messageType;
    private String message;
}
