package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IFriendRequestRepository;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendshipService implements IFriendshipService {

    private final IUserRepository userRepository;
    private final IFriendRequestRepository friendRequestRepository;

    @Override
    public List<SearchedUser> getAllPeople(String name, User sessionUser) {
        var allPeople = userRepository.searchUsers(name, sessionUser.getEmail());
        var acceptedFriends = getAllAcceptedFriends(sessionUser);
        var pendingFriends = getAllPendingFriends(sessionUser);

        return allPeople.stream()
                .filter(user -> isOnFriendList(acceptedFriends, user.getEmail()) == false)
                .filter(user -> isFriendshipBlocked(sessionUser.getEmail(), user.getEmail()) == false)
                .map(user -> new SearchedUser(user, isOnFriendList(pendingFriends, user.getEmail()) ? PENDING : NOT_FRIENDS_YET))
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchedUser> getAllAcceptedFriends(User sessionUser) {
        var acceptedRequests = friendRequestRepository.findFriendRequestByStatus(ACCEPTED, sessionUser.getEmail());
        return mapFriendRequestsOntoSearchedUsers(acceptedRequests, sessionUser.getEmail());
    }

    @Override
    public List<SearchedUser> getAllPendingFriends(User sessionUser) {
        var pendingRequests = friendRequestRepository.findFriendRequestByStatus(PENDING, sessionUser.getEmail());
        return mapFriendRequestsOntoSearchedUsers(pendingRequests, sessionUser.getEmail());
    }

    @Override
    public List<User> getAllReceivedPendingFriends(User sessionUser) {
        return friendRequestRepository.findAllUsersReceivedFriendRequests(sessionUser.getEmail());
    }

    @Override
    public List<User> getAllSentPendingFriends(User sessionUser) {
        return friendRequestRepository.findAllUsersSentFriendRequests(sessionUser.getEmail());
    }

    @Override
    public List<SearchedUser> getAllBlockedFriends(User sessionUser) {
        var blockedRequests = friendRequestRepository.findBlockedFriendRequestsBySessionUser(sessionUser.getEmail());
        return mapFriendRequestsOntoSearchedUsers(blockedRequests, sessionUser.getEmail());
    }

    @Override
    public boolean isAlreadyFriendOrPendingFriend(String email, User sessionUser) {
        return isFriend(email, sessionUser) || isPendingFriend(email, sessionUser);
    }

    @Override
    public void sendFriendRequest(User user, User sessionUser) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequestSender(sessionUser);
        friendRequest.setRequestReceiver(user);
        friendRequest.setStatus(PENDING);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public boolean isFriend(String email, User sessionUser) {
        return isOnFriendList(getAllAcceptedFriends(sessionUser), email);
    }

    @Override
    public boolean isPendingFriend(String email, User sessionUser) {
        return isOnFriendList(getAllPendingFriends(sessionUser), email);
    }

    @Override
    public void acceptFriendRequest(User sessionUser, User user) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findPendingFriendRequest(sessionUser.getEmail(), user.getEmail());
        friendRequest.get().setStatus(ACCEPTED);
        friendRequestRepository.save(friendRequest.get());
    }

    @Override
    public void deleteFriend(User sessionUser, User user) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findFriendRequest(sessionUser.getEmail(), user.getEmail());
        friendRequestRepository.delete(friendRequest.get());
    }

    @Override
    public void blockUser(FriendRequest friendRequest) {
        friendRequest.setStatus(BLOCKED);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public Optional<FriendRequest> getFriendRequestToBlock(String senderEmail, User receiver) {
        return friendRequestRepository.findPendingRequestToBeBlocked(senderEmail, receiver.getEmail());
    }

    @Override
    public boolean isFriendshipBlocked(String senderEmail, String receiverEmail) {
        return friendRequestRepository.findBlockedRequest(senderEmail, receiverEmail).isPresent() ||
               friendRequestRepository.findBlockedRequest(receiverEmail, senderEmail).isPresent();
    }

    @Override
    public Optional<FriendRequest> getBLockedFriendship(String senderEmail, String receiverEmail) {
        return friendRequestRepository.findBlockedRequest(senderEmail, receiverEmail);
    }

    private List<SearchedUser> mapFriendRequestsOntoSearchedUsers(List<FriendRequest> friendRequests, String sessionUserEmail) {
        return friendRequests
                .stream()
                .map(request -> request.getRequestReceiver().getEmail().equals(sessionUserEmail) ?
                        new SearchedUser(request.getRequestSender(), request.getStatus()) :
                        new SearchedUser(request.getRequestReceiver(), request.getStatus()))
                .collect(Collectors.toList());
    }

    private boolean isOnFriendList(List<SearchedUser> users, String searchedUsersEmail) {
        return users.stream()
                .filter(user -> user.getEmail().equals(searchedUsersEmail))
                .findFirst()
                .isPresent();
    }
}