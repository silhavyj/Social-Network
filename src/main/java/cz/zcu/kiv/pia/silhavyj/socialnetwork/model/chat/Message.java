package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat;

import lombok.*;

/***
 * Message data structure that is used in chat and WS
 * communication between two friends. This data structure
 * is used in two different scenarios - when a friend goes online/offline
 * and when a message is sent from one friend to another.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Message {

    /*** full name of the sender of the message */
    private String senderFullName;

    /*** e-mail address of the sender of the message */
    private String senderEmail;

    /*** path of the profile picture of the sender of the message */
    private String profilePicturePath;

    /*** type of the message */
    private MessageType messageType;

    /*** content of the message (plain text) */
    private String message;

    /*** message time stamp (when the message was sent off) */
    private String timeStamp;

    /***
     * Creates an instance of Message
     * @param senderFullName full name of the sender of the message
     * @param senderEmail e-mail address of the sender of the message
     * @param profilePicturePath path of the profile picture of the sender of the message
     * @param messageType type of the message
     */
    public Message(String senderFullName, String senderEmail, String profilePicturePath, MessageType messageType) {
        this.senderFullName = senderFullName;
        this.senderEmail = senderEmail;
        this.profilePicturePath = profilePicturePath;
        this.messageType = messageType;
    }
}
