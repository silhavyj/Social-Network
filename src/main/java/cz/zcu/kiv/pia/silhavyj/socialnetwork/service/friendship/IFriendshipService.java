package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

import java.util.List;
import java.util.Optional;

public interface IFriendshipService {

    List<SearchedUser> getAllPeople(String name, User sessionUser);
    List<SearchedUser> getAllAcceptedFriends(User sessionUser);
    List<SearchedUser> getAllPendingFriends(User sessionUser);
    List<User> getAllReceivedPendingFriends(User sessionUser);
    List<User> getAllSentPendingFriends(User sessionUser);
    List<SearchedUser> getAllBlockedFriends(User sessionUser);
    boolean isAlreadyFriendOrPendingFriend(String email, User sessionUser);
    void sendFriendRequest(User user, User sessionUser);
    boolean isFriend(String email, User sessionUser);
    boolean isPendingFriend(String email, User sessionUser);
    void acceptFriendRequest(User sessionUser, User user);
    void deleteFriend(User sessionUser, User user);
    void blockUser(FriendRequest friendRequest);
    Optional<FriendRequest> getFriendRequestToBlock(String senderEmail, User receiver);
    boolean isFriendshipBlocked(String senderEmail, String receiverEmail);
    Optional<FriendRequest> getBLockedFriendship(String senderEmail, String receiverEmail);
}
