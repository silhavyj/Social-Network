package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.post;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

import java.util.List;

/***
 * Method definitions of a post service.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public interface IPostService {

    /***
     * Creates a post
     * @param user instance of User who's creating the post
     * @param message plain-text content of the post
     */
    void createPost(User user, String message);

    /***
     * Returns a list of the latest user's posts
     * @param userEmail e-mail address of the user whose posts we want to retrieve
     * @return  list of the latest user's posts
     */
    List<Post> getUsersPosts(String userEmail);

    /***
     * Returns all posts an announcements to be displayed on the user's home page.
     * This list contains user's posts, all their friends' posts, and all announcements.
     * @param user instance of the session user
     * @return list of all posts and announcements
     */
    List<Post> getMainPagePosts(User user);

    /***
     * Creates an announcement. Announcements can be created only by administrators.
     * @param user instance of User who's creating the announcement
     * @param message plain-text content of the announcement
     */
    void createAnnouncement(User user, String message);

    /***
     * Returns a list of the latest user's announcements
     * @param userEmail e-mail address of the user whose announcements we want to retrieve
     * @return  list of the latest user's announcements
     */
    List<Post> getUsersAnnouncements(String userEmail);

    /***
     * Likes a post created by a friend (or an announcement created by one of the administrators)
     * @param postId id of the post/announcement
     * @param sessionUser session user - who's liking the post
     */
    void likePost(long postId, User sessionUser);

    /***
     * Unlikes a post created by a friend (or an announcement created by one of the administrators)
     * @param postId id of the post/announcement
     * @param sessionUser session user - who's unliking the post
     */
    void unlikePost(long postId, User sessionUser);
}
