package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IFriendRequestRepository;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IUserRepository;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequestStatus.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.ADMIN;

/***
 * This class handles all the logic related to relationships. For instance,
 * sending friend requests, blocking friends, searching users, etc.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@Transactional
@RequiredArgsConstructor
public class FriendshipService implements IFriendshipService {

    /*** implementation of IUserRepository (retrieving users from the database) */
    private final IUserRepository userRepository;

    /*** implementation of IFriendRequestRepository (storing friend requests to the database) */
    private final IFriendRequestRepository friendRequestRepository;

    /*** implementation of IRoleService (retrieving user roles from the database) */
    private final IRoleService roleService;

    /***
     * Returns a list of matching users after a search for a specific user(s)
     * @param name name (letters) provided from the client - what they're searching
     * @param sessionUser session User (who sent the HTTP request)
     * @return list of matching users
     */
    @Override
    public List<SearchedUser> getAllPeople(String name, User sessionUser) {
        // Retrieve all users from the database who "match" the name provided by the client.
        var allPeople = userRepository.searchUsers(name, sessionUser.getEmail());

        // Retrieve all session user's friends.
        var acceptedFriends = getAllAcceptedFriends(sessionUser);

        // Retrieve all session user's pending requests.
        var pendingFriends = getAllPendingFriends(sessionUser);

        // Filer out all session user's friends as well as their "pending friends"
        // and return the result list of the search.
        return allPeople.stream()
                .filter(user -> isOnFriendList(acceptedFriends, user.getEmail()) == false)
                .filter(user -> isFriendshipBlocked(sessionUser.getEmail(), user.getEmail()) == false)
                .map(user -> new SearchedUser(user, isOnFriendList(pendingFriends, user.getEmail()) ? PENDING : NOT_FRIENDS_YET))
                .collect(Collectors.toList());
    }

    /***
     * Returns a list of all user's friends who happen to be administrators
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all user's friends who are administrators
     */
    @Override
    public List<SearchedUser> getAllAcceptedFriendsAdmins(User sessionUser) {
        var acceptedRequests = friendRequestRepository.findFriendRequestByStatus(ACCEPTED, sessionUser.getEmail());
        var friendsAdmins = mapFriendRequestsOntoSearchedUsersWithRole(acceptedRequests, sessionUser.getEmail(), true);
        return friendsAdmins;
    }

    /***
     * Returns a list of all user's friends who don't have administrator privileges
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all user's friends who aren't administrators
     */
    @Override
    public List<SearchedUser> getAllAcceptedFriendsNotAdmins(User sessionUser) {
        var acceptedRequests = friendRequestRepository.findFriendRequestByStatus(ACCEPTED, sessionUser.getEmail());
        var friendsNotAdmins =  mapFriendRequestsOntoSearchedUsersWithRole(acceptedRequests, sessionUser.getEmail(), false);
        return friendsNotAdmins;
    }

    /***
     * Returns a list of all user's friends regardless of the friend being an administrator
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all user's friends
     */
    @Override
    public List<SearchedUser> getAllAcceptedFriends(User sessionUser) {
        var acceptedRequests = friendRequestRepository.findFriendRequestByStatus(ACCEPTED, sessionUser.getEmail());
        return mapFriendRequestsOntoSearchedUsers(acceptedRequests, sessionUser.getEmail());
    }

    /***
     * Returns a list of all user's friends
     * @param userEmail e-mail address of the session user
     * @return list of all user's friends
     */
    @Override
    public List<User> getAllAcceptedFriends(String userEmail) {
        // Create an instance of a list that will be returned.
        List<User> friends = new ArrayList<>();

        // Load up all user's friends.
        var acceptedRequests = friendRequestRepository.findFriendRequestByStatus(ACCEPTED, userEmail);

        // Filter off the user's itself (from the requests) and collect only who they're friends with only.
        for (var friendRequest : acceptedRequests) {
            if (friendRequest.getRequestReceiver().getEmail().equals(userEmail)) {
                friends.add(friendRequest.getRequestSender());
            } else {
                friends.add(friendRequest.getRequestReceiver());
            }
        }
        // Return the list of user's friends.
        return friends;
    }

    /***
     * Returns all user's pending friends
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all user's pending friends
     */
    @Override
    public List<SearchedUser> getAllPendingFriends(User sessionUser) {
        var pendingRequests = friendRequestRepository.findFriendRequestByStatus(PENDING, sessionUser.getEmail());
        return mapFriendRequestsOntoSearchedUsers(pendingRequests, sessionUser.getEmail());
    }

    /***
     * Return a list of all user's from whom the session user has received a friend request
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of user's from whom the session user has received a friend request
     */
    @Override
    public List<User> getAllReceivedPendingFriends(User sessionUser) {
        return friendRequestRepository.findAllUsersReceivedPendingFriendRequests(sessionUser.getEmail());
    }

    /***
     * Return a list of all user's to whom the session user has sent a friend request
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of user's too whom the session user has sent a friend request
     */
    @Override
    public List<User> getAllSentPendingFriends(User sessionUser) {
        return friendRequestRepository.findAllUsersSentPendingFriendRequests(sessionUser.getEmail());
    }

    /***
     * Returns a list of all users who the session user has blocked.
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all users who the session user has blocked.
     */
    @Override
    public List<SearchedUser> getAllBlockedFriends(User sessionUser) {
        var blockedRequests = friendRequestRepository.findBlockedFriendRequestsBySessionUser(sessionUser.getEmail());
        return mapFriendRequestsOntoSearchedUsers(blockedRequests, sessionUser.getEmail());
    }

    /***
     * Returns true/false depending on whether the two users are already friends or there is
     * a pending requests waiting to be reacted to by either of the users.
     * @param email e-mail address of the first user
     * @param sessionUser session user - the second user
     * @return true/false depending on whether the two users are already friends or there is
     *         a pending requests waiting to be reacted to by either of the users.
     */
    @Override
    public boolean isAlreadyFriendOrPendingFriend(String email, User sessionUser) {
        return isFriend(email, sessionUser) || isPendingFriend(email, sessionUser);
    }

    /***
     * Sends a friend request to the user (stores the request into the database)
     * @param user user who is going to receive the friend request
     * @param sessionUser user who is sending the friend request
     */
    @Override
    public void sendFriendRequest(User user, User sessionUser) {
        // Create an instance of FriendRequest.
        FriendRequest friendRequest = new FriendRequest();

        // Set the sender and receiver of the friend request.
        friendRequest.setRequestSender(sessionUser);
        friendRequest.setRequestReceiver(user);

        // Set the request status as pending and store it into the database.
        friendRequest.setStatus(PENDING);
        friendRequestRepository.save(friendRequest);
    }

    /***
     * Check if two the users are friends or not.
     * @param email e-mail address of the first user
     * @param sessionUser session user - the second user
     * @return true, if the two users are friend, false otherwise
     */
    @Override
    public boolean isFriend(String email, User sessionUser) {
        return isOnFriendList(getAllAcceptedFriends(sessionUser), email);
    }

    /***
     * Check if two the users are "pending friends" or not.
     * @param email e-mail address of the first user
     * @param sessionUser session user - the second user
     * @return true, if the two users are "pending friends", false otherwise
     */
    @Override
    public boolean isPendingFriend(String email, User sessionUser) {
        return isOnFriendList(getAllPendingFriends(sessionUser), email);
    }

    /***
     * Accepts a pending friend request
     * @param sessionUser session user (who is accepting the friend request)
     * @param user user who sent the friend request
     */
    @Override
    public void acceptFriendRequest(User sessionUser, User user) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findPendingFriendRequest(sessionUser.getEmail(), user.getEmail());
        friendRequest.get().setStatus(ACCEPTED);
        friendRequestRepository.save(friendRequest.get());
    }

    /***
     * Deletes an accepted friend request (deletes a user as a friend)
     * @param sessionUser session user (who is deleting the other user)
     * @param user user who is going to be removed from the session user's friend list
     */
    @Override
    public void deleteFriend(User sessionUser, User user) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findFriendRequest(sessionUser.getEmail(), user.getEmail());
        friendRequestRepository.delete(friendRequest.get());
    }

    /***
     * Blocks the friend request given as a parameter
     * @param friendRequest friend request to be blocked
     */
    @Override
    public void blockUser(FriendRequest friendRequest) {
        friendRequest.setStatus(BLOCKED);
        friendRequestRepository.save(friendRequest);
    }

    /***
     * Returns a pending request to be blocked.
     * @param senderEmail e-mail address of the sender of the friend request
     * @param receiver e-mail address of the receiver of the friend request
     * @return pending request to be blocked
     */
    @Override
    public Optional<FriendRequest> getFriendRequestToBlock(String senderEmail, User receiver) {
        return friendRequestRepository.findPendingRequestToBeBlocked(senderEmail, receiver.getEmail());
    }

    /***
     * Return true/false depending on whether either of the users has blocked the other one
     * @param senderEmail e-mail address of the sender of the friend request
     * @param receiverEmail e-mail address of the receiver of the friend request
     * @return true/false depending on whether either of the users has blocked the other one
     */
    @Override
    public boolean isFriendshipBlocked(String senderEmail, String receiverEmail) {
        return friendRequestRepository.findBlockedRequest(senderEmail, receiverEmail).isPresent() ||
               friendRequestRepository.findBlockedRequest(receiverEmail, senderEmail).isPresent();
    }

    /***
     * Returns a blocked friend request
     * @param senderEmail e-mail address of the sender of the friend request
     * @param receiverEmail e-mail address of the receiver of the friend request (who blocked the other user)
     * @return blocked friend request
     */
    @Override
    public Optional<FriendRequest> getBLockedFriendship(String senderEmail, String receiverEmail) {
        return friendRequestRepository.findBlockedRequest(senderEmail, receiverEmail);
    }

    /***
     * Maps a friend request onto SearchedUser (a data structure returned to the frond end).
     * The role of the other user in this case doesn't matter.
     * @param friendRequests list of friend requests
     * @param sessionUserEmail e-mail address of the session user (so we can filter them out)
     * @return list of SearchedUser
     */
    private List<SearchedUser> mapFriendRequestsOntoSearchedUsers(List<FriendRequest> friendRequests, String sessionUserEmail) {
        // Essentially, we need to filer out the session user because all we, as the front end, are
        // interested in is the other user (not us).
        return friendRequests
                .stream()
                .map(request -> request.getRequestReceiver().getEmail().equals(sessionUserEmail) ?
                        new SearchedUser(request.getRequestSender(), request.getStatus()) :
                        new SearchedUser(request.getRequestReceiver(), request.getStatus()))
                .collect(Collectors.toList());
    }

    /***
     * Maps a friend request onto SearchedUser (a data structure returned to the frond end).
     * The role of the other user in this case DOES matter as we want to get either
     * only administrator or only users.
     * @param friendRequests list of friend requests
     * @param sessionUserEmail e-mail address of the session user (so we can filter them out)
     * @param roleAdmin true/false whether the final list is supposed to contain administrators or users
     * @return list of SearchedUser
     */
    private List<SearchedUser> mapFriendRequestsOntoSearchedUsersWithRole(List<FriendRequest> friendRequests, String sessionUserEmail, boolean roleAdmin) {
        // Retrieve the ADMIN role from the database.
        Role adminRole = roleService.getRoleByUserRole(ADMIN).get();

        // First off, filer out the users who don't have the desired role
        // and then, map them onto SearchedUser.
        return friendRequests
                .stream()
                .filter(friendRequest -> {
                   boolean isAdmin = friendRequest.getRequestReceiver().getEmail().equals(sessionUserEmail) ?
                            friendRequest.getRequestSender().getRoles().contains(adminRole) :
                            friendRequest.getRequestReceiver().getRoles().contains(adminRole);
                   return roleAdmin ? isAdmin : !isAdmin;
                })
                .map(request -> request.getRequestReceiver().getEmail().equals(sessionUserEmail) ?
                        new SearchedUser(request.getRequestSender(), request.getStatus()) :
                        new SearchedUser(request.getRequestReceiver(), request.getStatus()))
                .collect(Collectors.toList());
    }

    /***
     * Returns true/false depending on whether a user is listed on the list of user given as a parameter
     * @param users list of user (pending, blocked, accepted, ...)
     * @param searchedUsersEmail e-mail address of the user we want to find out if they're on the list
     * @return true/false depending on whether a user is listed on the list of user given as a parameter
     */
    private boolean isOnFriendList(List<SearchedUser> users, String searchedUsersEmail) {
        return users.stream()
                .filter(user -> user.getEmail().equals(searchedUsersEmail))
                .findFirst()
                .isPresent();
    }
}