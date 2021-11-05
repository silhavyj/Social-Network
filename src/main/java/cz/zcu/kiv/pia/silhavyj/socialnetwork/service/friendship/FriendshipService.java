package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IFriendRequestRepository;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.ACCEPTED;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING;

@Service
@RequiredArgsConstructor
public class FriendshipService implements IFriendshipService {

    private final IFriendRequestRepository friendRequestRepository;
    private final IUserService userService;

    @Override
    public List<User> getAllUsersFriends(String email) {
        return getAllUsersFriends(email, ACCEPTED);
    }

    @Override
    public List<User> getAllUsersPendingFriends(String email) {
        return getAllUsersFriends(email, PENDING);
    }

    @Override
    public List<SearchedUser> getAllSearchedUser(String name, String sessionUserEmail) {
        var friends = getAllUsersFriends(sessionUserEmail);
        var pendingFriends = getAllUsersPendingFriends(sessionUserEmail);
        return userService.searchUsers(name, sessionUserEmail).stream()
                .filter(user -> isOnFriendList(friends, user) == false)
                .map(user -> new SearchedUser(user, isOnFriendList(pendingFriends, user)))
                .collect(Collectors.toList());
    }

    private boolean isOnFriendList(List<User> friends, User potentialFriend) {
        return friends.stream()
                .filter(user -> user.getEmail().equals(potentialFriend.getEmail()))
                .findFirst()
                .isPresent();
    }

    private List<User> getAllUsersFriends(String email, FriendRequestStatus status) {
        return friendRequestRepository.getAllUsersFriendRequests(email)
                .stream()
                .filter(request -> request.getStatus().equals(status))
                .map(request -> request.getRequestReceiver().getEmail().equals(email) ? request.getRequestSender() : request.getRequestReceiver())
                .collect(Collectors.toList());
    }
}
