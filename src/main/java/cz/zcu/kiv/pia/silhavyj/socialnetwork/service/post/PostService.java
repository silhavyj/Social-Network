package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.post;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Like;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.ILikeRepository;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IPostRepository;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship.IFriendshipService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.ANNOUNCEMENT;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.NORMAL_POST;

/***
 * This class handles all the logic related to posts. For instance,
 * creating a post or an announcement, liking or unliking posts, etc.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PostService implements IPostService {

    /*** implementation of IPostRepository (retrieving posts from the database) */
    private final IPostRepository postRepository;

    /*** instance of AppConfiguration (number of posts to be displayed on the main page) */
    private final AppConfiguration appConfiguration;

    /*** implementation of IFriendshipService (getting the list of all user's friends) */
    private final IFriendshipService friendshipService;

    /*** implementation of IUserService (retrieving admins from the database) */
    private final IUserService userService;

    /*** implementation of ILikeRepository (storing liked into the database) */
    private final ILikeRepository likeRepository;

    /***
     * Creates a post
     * @param user instance of User who's creating the post
     * @param message plain-text content of the post
     */
    @Override
    public void createPost(User user, String message) {
        Post post = new Post(user, message, NORMAL_POST);
        postRepository.save(post);
    }

    /***
     * Returns a list of the latest user's posts
     * @param userEmail e-mail address of the user whose posts we want to retrieve
     * @return  list of the latest user's posts
     */
    @Override
    public List<Post> getUsersPosts(String userEmail) {
        return postRepository.findTopUsersPosts(userEmail, PageRequest.of(0, appConfiguration.getPostsToDisplayOnProfilePage()));
    }

    /***
     * Returns all posts an announcements to be displayed on the user's home page.
     * This list contains user's posts, all their friends' posts, and all announcements.
     * @param user instance of the session user
     * @return list of all posts and announcements
     */
    @Override
    public List<Post> getMainPagePosts(User user) {
        // Create a list of posts to be returned at the end of this method.
        List<Post> posts = getUsersPosts(user.getEmail());

        // Load up all user's friends and append their posts to the final list.
        var friends = friendshipService.getAllAcceptedFriends(user);
        for (var friend : friends) {
            posts.addAll(getUsersPosts(friend.getEmail()));
        }

        // Load up all admins and append their announcements to the final list.
        List<User> admins = userService.getAdmins();
        for (var admin : admins) {
            posts.addAll(getUsersAnnouncements(admin.getEmail()));
        }

        // Sort out the list by when they were created and limit the total
        // number of them down to 10 (only latest 10 posts/announcements will be displayed)
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPostedAt).reversed())
                .limit(appConfiguration.getPostsToDisplayOnProfilePage())
                .collect(Collectors.toList());
    }

    /***
     * Creates an announcement. Announcements can be created only by administrators.
     * @param user instance of User who's creating the announcement
     * @param message plain-text content of the announcement
     */
    @Override
    public void createAnnouncement(User user, String message) {
        Post post = new Post(user, message, ANNOUNCEMENT);
        postRepository.save(post);
    }

    /***
     * Returns a list of the latest user's announcements
     * @param userEmail e-mail address of the user whose announcements we want to retrieve
     * @return  list of the latest user's announcements
     */
    @Override
    public List<Post> getUsersAnnouncements(String userEmail) {
        return postRepository.findTopUsersAnnouncements(userEmail, PageRequest.of(0, appConfiguration.getPostsToDisplayOnProfilePage()));
    }

    /***
     * Likes a post created by a friend (or an announcement created by one of the administrators)
     * @param postId id of the post/announcement
     * @param sessionUser session user - who's liking the post
     */
    @Override
    public void likePost(long postId, User sessionUser) {
        // Retrieve the post/announcement from the database.
        Optional<Post> post = Optional.of(postRepository.getById(postId));
        if (post.isEmpty()) {
            return;
        }

        // Make sure the user hasn't liked the post yet.
        boolean alreadyLiked = post.get().getLikes()
                .stream()
                .filter(like -> like.getUser().getEmail().equals(sessionUser.getEmail()))
                .findFirst()
                .isPresent();

        // If the user has already liked the post, or they're the one who created the post,
        // do not like the post again.
        if (alreadyLiked == true || post.get().getUser().getEmail().equals(sessionUser.getEmail())) {
            return;
        }

        // Like the post and update the record in the database.
        post.get().getLikes().add(new Like(sessionUser));
        postRepository.save(post.get());
    }

    /***
     * Unlikes a post created by a friend (or an announcement created by one of the administrators)
     * @param postId id of the post/announcement
     * @param sessionUser session user - who's unliking the post
     */
    @Override
    public void unlikePost(long postId, User sessionUser) {
        // Retrieve the post/announcement from the database.
        Optional<Post> post = Optional.of(postRepository.getById(postId));
        if (post.isEmpty())
            return;

        // Go through the likes of the post and get the one belonging
        // to the user who wants to unlike it.
        Optional<Like> likeToDelete = post.get().getLikes()
                        .stream()
                        .filter(like -> like.getUser().getEmail().equals(sessionUser.getEmail()))
                        .findFirst();

        // Make sure such a like exists, and delete it from the post.
        if (likeToDelete.isPresent()) {
            post.get().getLikes().removeIf(like -> like.getUser().getEmail().equals(sessionUser.getEmail()));
            postRepository.save(post.get());
            likeRepository.delete(likeToDelete.get());
        }
    }
}
