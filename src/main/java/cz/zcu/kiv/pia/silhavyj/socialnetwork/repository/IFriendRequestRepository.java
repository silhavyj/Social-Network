package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT request from FriendRequest request WHERE request.requestReceiver.email = ?1 OR request.requestSender.email = ?1")
    List<FriendRequest> findAllUsersFriendRequests(String email);

    @Query("SELECT request.requestSender from FriendRequest request WHERE request.requestReceiver.email = ?1 AND request.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    List<User> findAllUsersReceivedFriendRequests(String email);

    @Query("SELECT request.requestReceiver from FriendRequest request WHERE request.requestSender.email = ?1 AND request.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    List<User> findAllUsersSentFriendRequests(String email);

    @Query("SELECT request from FriendRequest request WHERE request.requestReceiver.email = ?1 AND request.requestSender.email = ?2 AND request.status = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING")
    Optional<FriendRequest> findPendingFriendRequest(String emailReceiver, String emailSender);

    @Query("SELECT request from FriendRequest request WHERE (request.requestReceiver.email = ?1 AND request.requestSender.email = ?2) OR (request.requestReceiver.email = ?2 AND request.requestSender.email = ?1)")
    Optional<FriendRequest> findFriendRequest(String email, String email1);
}
