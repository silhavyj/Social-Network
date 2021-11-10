package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.status = ?1 AND (rqst.requestSender.email = ?2 OR rqst.requestReceiver.email = ?2)")
    List<FriendRequest> findFriendRequestByStatus(FriendRequestStatus status, String sessionUserEmail);

    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.status =  cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.BLOCKED AND rqst.requestReceiver.email = ?1")
    List<FriendRequest> findBlockedFriendRequestsBySessionUser(String sessionUserEmail);

    @Query("SELECT rqst.requestSender from FriendRequest rqst WHERE rqst.requestReceiver.email = ?1 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    List<User> findAllUsersReceivedFriendRequests(String email);

    @Query("SELECT rqst.requestReceiver from FriendRequest rqst WHERE rqst.requestSender.email = ?1 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    List<User> findAllUsersSentFriendRequests(String email);

    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.requestReceiver.email = ?1 AND rqst.requestSender.email = ?2 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    Optional<FriendRequest> findPendingFriendRequest(String emailReceiver, String emailSender);

    @Query("SELECT rqst from FriendRequest rqst WHERE (rqst.requestReceiver.email = ?1 AND rqst.requestSender.email = ?2) OR (rqst.requestReceiver.email = ?2 AND rqst.requestSender.email = ?1)")
    Optional<FriendRequest> findFriendRequest(String email, String email1);

    @Query("SELECT rqst from FriendRequest rqst WHERE rqst.requestSender.email = ?1 AND rqst.requestReceiver.email = ?2 AND rqst.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    Optional<FriendRequest> findPendingRequestToBeBlocked(String senderEmail, String receiverEmail);
}
