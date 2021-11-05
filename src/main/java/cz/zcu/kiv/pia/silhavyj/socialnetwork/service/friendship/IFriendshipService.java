package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

import java.util.List;

public interface IFriendshipService {

    List<User> getAllUsersFriends(String email);
    List<User> getAllUsersPendingFriends(String email);
    List<SearchedUser> getAllSearchedUser(String name, String sessionUserEmail);
}
