package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship;

/***
 * Status of a friend request.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public enum FriendRequestStatus {

    /*** helper state used later on in Thymeleaf (when searching users) */
    NOT_FRIENDS_YET,

    /*** friend request has been accepted (the two users have now become friends) */
    ACCEPTED,

    /*** friend request is still pending */
    PENDING,

    /*** the receiver has blocked the sender */
    BLOCKED
}
