package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/***
 * JPA repository that handles SQL queries regarding friend requests.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Repository
public interface IFriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    /***
     * Returns a list of friend requests by a particular status. The user issuing this
     * request is typically the session user and therefore, they need to be either the sender
     * of the friend request or the receiver.
     * @param status status of the friend request PENDING, BLOCKED, ...
     * @param sessionUserEmail e-mail address of the session user
     * @return list of matching friend requests
     */
    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.status = ?1 AND (rqst.requestSender.email = ?2 OR rqst.requestReceiver.email = ?2)")
    List<FriendRequest> findFriendRequestByStatus(FriendRequestStatus status, String sessionUserEmail);

    /***
     * Returns a list of friend requests that have been blocked by the session user.
     * They must be therefore the receiver of the friend request, and the status of the friend
     * request has to be BLOCKED.
     * @param sessionUserEmail e-mail address of the session user
     * @return list of matching friend requests
     */
    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.BLOCKED AND rqst.requestReceiver.email = ?1")
    List<FriendRequest> findBlockedFriendRequestsBySessionUser(String sessionUserEmail);

    /***
     * Returns a list of user's received pending friend requests
     * @param email e-mail address of the user
     * @return list of matching friend requests
     */
    @Query("SELECT rqst.requestSender from FriendRequest rqst WHERE rqst.requestReceiver.email = ?1 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    List<User> findAllUsersReceivedPendingFriendRequests(String email);

    /***
     * Returns a list of user's sent pending friend requests
     * @param email e-mail address of the user
     * @return a list of matching friend requests
     */
    @Query("SELECT rqst.requestReceiver from FriendRequest rqst WHERE rqst.requestSender.email = ?1 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    List<User> findAllUsersSentPendingFriendRequests(String email);

    /***
     * Returns an instance of a pending friend request.
     * @param emailReceiver e-mail address of the received of the friend request
     * @param emailSender e-mail address of the sender of the friend request
     * @return matching friend request (Optional - can be null)
     */
    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.requestReceiver.email = ?1 AND rqst.requestSender.email = ?2 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    Optional<FriendRequest> findPendingFriendRequest(String emailReceiver, String emailSender);

    /***
     * Returns a friend request involving the two users (the order of the sender and receiver does not matter, they just have to be involved)
     * @param email e-mail address of the first user
     * @param email1 e-mail address of the second user
     * @return matching friend request (Optional - can be null)
     */
    @Query("SELECT rqst from FriendRequest rqst WHERE (rqst.requestReceiver.email = ?1 AND rqst.requestSender.email = ?2) OR (rqst.requestReceiver.email = ?2 AND rqst.requestSender.email = ?1)")
    Optional<FriendRequest> findFriendRequest(String email, String email1);

    /***
     * Returns a pending friend request with a particular sender and receiver. The receiver wants to block the sender.
     * @param senderEmail e-mail address of the sender of the friend request (user to be BLOCKED)
     * @param receiverEmail e-mail address of the receiver of the friend request
     * @return matching friend request (Optional - can be null)
     */
    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.requestSender.email = ?1 AND rqst.requestReceiver.email = ?2 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    Optional<FriendRequest> findPendingRequestToBeBlocked(String senderEmail, String receiverEmail);

    /***
     * Returns a blocked friend request with a particular sender and receiver. The receiver wants to unblock the sender.
     * @param senderEmail e-mail address of the sender of the friend request (user to be UNBLOCKED)
     * @param receiverEmail e-mail address of the receiver of the friend request
     * @return matching friend request (Optional - can be null)
     */
    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.requestSender.email = ?1 AND rqst.requestReceiver.email = ?2 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.BLOCKED")
    Optional<FriendRequest> findBlockedRequest(String senderEmail, String receiverEmail);
}
