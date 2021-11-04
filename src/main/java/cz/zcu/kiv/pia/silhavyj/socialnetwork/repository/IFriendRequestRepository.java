package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFriendRequestRepository extends JpaRepository<Token, Long> {

    @Query("SELECT request from FriendRequest request WHERE request.requestReceiver.email = ?1 OR request.requestSender.email = ?1")
    List<FriendRequest> getAllUsersFriendRequests(String email);
}
