package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IFriendRequestRepository;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.ACCEPTED;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.PENDING;

@Service
@RequiredArgsConstructor
public class FriendshipService implements IFriendshipService {

    private final IFriendRequestRepository friendRequestRepository;
    private final IUserService userService;

    @Override
    public List<User> getAllUsersFriends(String sessionUserEmail) {
        return getAllUsersFriends(sessionUserEmail, ACCEPTED);
    }

    @Override
    public List<User> getAllUsersPendingFriends(String sessionUserEmail) {
        return getAllUsersFriends(sessionUserEmail, PENDING);
    }

    @Override
    public List<User> getAllUsersReceivedPendingFriends(String sessionUserEmail) {
        return friendRequestRepository.findAllUsersReceivedFriendRequests(sessionUserEmail);
    }

    @Override
    public List<User> getAllUsersSentPendingFriends(String sessionUserEmail) {
        return friendRequestRepository.findAllUsersSentFriendRequests(sessionUserEmail);
    }

    @Override
    public boolean isPendingFriend(String email, String sessionUserEmail) {
        return isOnFriendList(getAllUsersPendingFriends(sessionUserEmail), email);
    }

    @Override
    public boolean isFriend(String email, String sessionUserEmail) {
        return isOnFriendList(getAllUsersFriends(sessionUserEmail), email);
    }

    @Override
    public boolean isAlreadyFriendOrPendingFriend(String email, String sessionUserEmail) {
       return isOnFriendList(getAllUsersFriends(sessionUserEmail), email) ||
              isOnFriendList(getAllUsersPendingFriends(sessionUserEmail), email);
    }

    @Override
    public List<SearchedUser> getAllSearchedUser(String name, String sessionUserEmail) {
        var friends = getAllUsersFriends(sessionUserEmail);
        var pendingFriends = getAllUsersPendingFriends(sessionUserEmail);
        return userService.searchUsers(name, sessionUserEmail).stream()
                .filter(user -> isOnFriendList(friends, user.getEmail()) == false)
                .map(user -> new SearchedUser(user, isOnFriendList(pendingFriends, user.getEmail())))
                .collect(Collectors.toList());
    }

    @Override
    public void sendFriendRequest(User receiver, User sender) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequestSender(sender);
        friendRequest.setRequestReceiver(receiver);
        friendRequest.setStatus(PENDING);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public void acceptFriendRequest(User receiver, User sender) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findPendingFriendRequest(receiver.getEmail(), sender.getEmail());
        friendRequest.get().setStatus(ACCEPTED);
        friendRequestRepository.save(friendRequest.get());
    }

    @Override
    public void deleteFriend(User sessionUser, User user) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findFriendRequest(sessionUser.getEmail(), user.getEmail());
        friendRequestRepository.delete(friendRequest.get());
    }

    private boolean isOnFriendList(List<User> friends, String potentialFriendEmail) {
        return friends.stream()
                .filter(user -> user.getEmail().equals(potentialFriendEmail))
                .findFirst()
                .isPresent();
    }

    private List<User> getAllUsersFriends(String email, FriendRequestStatus status) {
        return friendRequestRepository.findAllUsersFriendRequests(email)
                .stream()
                .filter(request -> request.getStatus().equals(status))
                .map(request -> request.getRequestReceiver().getEmail().equals(email) ? request.getRequestSender() : request.getRequestReceiver())
                .collect(Collectors.toList());
    }
}
