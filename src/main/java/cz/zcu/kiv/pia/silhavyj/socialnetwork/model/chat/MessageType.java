package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat;

/***
 * Type of the message sent through web sockets
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public enum MessageType {

    /*** notification of a friend being now online */
    USER_ONLINE,

    /*** notification of a friend going offline */
    USER_OFFLINE,

    /*** chat message sent from one friend to another */
    MESSAGE
}
