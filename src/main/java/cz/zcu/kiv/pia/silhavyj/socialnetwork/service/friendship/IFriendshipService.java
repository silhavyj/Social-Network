package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

import java.util.List;

public interface IFriendshipService {

    List<User> getAllUsersFriends(String sessionUserEmail);
    List<User> getAllUsersPendingFriends(String sessionUserEmail);
    List<User> getAllUsersReceivedPendingFriends(String sessionUserEmail);
    List<User> getAllUsersSentPendingFriends(String sessionUserEmail);
    boolean isPendingFriend(String email, String sessionUserEmail);
    boolean isFriend(String email, String sessionUserEmail);
    boolean isAlreadyFriendOrPendingFriend(String email, String sessionUserEmail);
    List<SearchedUser> getAllSearchedUser(String name, String sessionUserEmail);
    void sendFriendRequest(User receiver, User sender);
    void acceptFriendRequest(User receiver, User sender);
    void deleteFriend(User sessionUser, User user);
}
