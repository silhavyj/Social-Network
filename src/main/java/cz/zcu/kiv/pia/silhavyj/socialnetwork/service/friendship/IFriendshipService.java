package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.FriendRequest;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship.SearchedUser;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

import java.util.List;
import java.util.Optional;

/***
 * Method definitions of a friendship service.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public interface IFriendshipService {

    /***
     * Returns a list of matching users after a search for a specific user(s)
     * @param name name (letters) provided from the client - what they're searching
     * @param sessionUser session User (who sent the HTTP request)
     * @return list of matching users
     */
    List<SearchedUser> getAllPeople(String name, User sessionUser);

    /***
     * Returns a list of all user's friends who happen to be administrators
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all user's friends who are administrators
     */
    List<SearchedUser> getAllAcceptedFriendsAdmins(User sessionUser);

    /***
     * Returns a list of all user's friends who don't have administrator privileges
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all user's friends who aren't administrators
     */
    List<SearchedUser> getAllAcceptedFriendsNotAdmins(User sessionUser);

    /***
     * Returns a list of all user's friends regardless of the friend being an administrator
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all user's friends
     */
    List<SearchedUser> getAllAcceptedFriends(User sessionUser);

    /***
     * Returns a list of all user's friends
     * @param userEmail e-mail address of the session user
     * @return list of all user's friends
     */
    List<User> getAllAcceptedFriends(String userEmail);

    /***
     * Returns all user's pending friends
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all user's pending friends
     */
    List<SearchedUser> getAllPendingFriends(User sessionUser);

    /***
     * Return a list of all user's from whom the session user has received a friend request
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of user's from whom the session user has received a friend request
     */
    List<User> getAllReceivedPendingFriends(User sessionUser);

    /***
     * Return a list of all user's to whom the session user has sent a friend request
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of user's too whom the session user has sent a friend request
     */
    List<User> getAllSentPendingFriends(User sessionUser);

    /***
     * Returns a list of all users who the session user has blocked.
     * @param sessionUser session user (who sent the HTTP request)
     * @return list of all users who the session user has blocked.
     */
    List<SearchedUser> getAllBlockedFriends(User sessionUser);

    /***
     * Returns true/false depending on whether the two users are already friends or there is
     * a pending requests waiting to be reacted to by either of the users.
     * @param email e-mail address of the first user
     * @param sessionUser session user - the second user
     * @return true/false depending on whether the two users are already friends or there is
     *         a pending requests waiting to be reacted to by either of the users.
     */
    boolean isAlreadyFriendOrPendingFriend(String email, User sessionUser);

    /***
     * Sends a friend request to the user (stores the request into the database)
     * @param user user who is going to receive the friend request
     * @param sessionUser user who is sending the friend request
     */
    void sendFriendRequest(User user, User sessionUser);

    /***
     * Check if two the users are friends or not.
     * @param email e-mail address of the first user
     * @param sessionUser session user - the second user
     * @return true, if the two users are friend, false otherwise
     */
    boolean isFriend(String email, User sessionUser);

    /***
     * Check if two the users are "pending friends" or not.
     * @param email e-mail address of the first user
     * @param sessionUser session user - the second user
     * @return true, if the two users are "pending friends", false otherwise
     */
    boolean isPendingFriend(String email, User sessionUser);

    /***
     * Accepts a pending friend request
     * @param sessionUser session user (who is accepting the friend request)
     * @param user user who sent the friend request
     */
    void acceptFriendRequest(User sessionUser, User user);

    /***
     * Deletes an accepted friend request (deletes a user as a friend)
     * @param sessionUser session user (who is deleting the other user)
     * @param user user who is going to be removed from the session user's friend list
     */
    void deleteFriend(User sessionUser, User user);

    /***
     * Blocks the friend request given as a parameter
     * @param friendRequest friend request to be blocked
     */
    void blockUser(FriendRequest friendRequest);

    /***
     * Returns a pending request to be blocked.
     * @param senderEmail e-mail address of the sender of the friend request
     * @param receiver e-mail address of the receiver of the friend request
     * @return pending request to be blocked
     */
    Optional<FriendRequest> getFriendRequestToBlock(String senderEmail, User receiver);

    /***
     * Return true/false depending on whether either of the users has blocked the other one
     * @param senderEmail e-mail address of the sender of the friend request
     * @param receiverEmail e-mail address of the receiver of the friend request
     * @return true/false depending on whether either of the users has blocked the other one
     */
    boolean isFriendshipBlocked(String senderEmail, String receiverEmail);

    /***
     * Returns a blocked friend request
     * @param senderEmail e-mail address of the sender of the friend request
     * @param receiverEmail e-mail address of the receiver of the friend request (who blocked the other user)
     * @return blocked friend request
     */
    Optional<FriendRequest> getBLockedFriendship(String senderEmail, String receiverEmail);
}