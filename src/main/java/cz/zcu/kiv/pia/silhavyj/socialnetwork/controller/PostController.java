package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.post.IPostService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * Controller used for managing posts and announcements.
 *
 * GET  /posts               - returns a list of the latest posts of all user's friends as well as announcements created by all admins (10)
 * POST /posts/normal-posts  - creates a posts
 * GET  /posts/normal-posts  - returns a list of user's latest posts (10)
 * POST /posts/announcements - creates an announcement (only admins can post an announcement)
 * GET  /posts/announcements - returns a list of user's latest announcements (10)
 * POST /posts/{id}/likes    - likes a post
 * DELETE /posts/{id}/likes  - unlikes a post
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    /*** implementation of IPostService (managing posts) */
    private final IPostService postService;

    /*** implementation of IUserService (retrieving users from the database) */
    private final IUserService userService;

    /***
     * Creates a post.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param message the post message itself
     */
    @PostMapping("/normal-posts")
    public void addOrdinaryPost(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                @RequestBody String message) {
        // Get the session user and create a post with the message given as a parameter
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        postService.createPost(sessionUser, message);
    }

    /***
     * Returns a list of the latest user's posts.
     * This list gets displayed on the profile page of the application.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @return a lif the user's latest posts
     */
    @GetMapping("/normal-posts")
    public List<Post> getLatestUsersPosts(@CurrentSecurityContext(expression="authentication") Authentication authentication) {
        String email = authentication.getName();
        return postService.getUsersPosts(email);
    }

    /***
     * Creates an announcement. This endpoint is only accessibly by administrators.
     * Announcements are visible by all users regardless of the friendship.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param message the announcement message itself
     */
    @PostMapping("/announcements")
    public void addAnnouncements(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                 @RequestBody String message) {
        // Get the session user and create an announcement with the message given as a parameter
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        postService.createAnnouncement(sessionUser, message);
    }

    /***
     * Returns a list of the latest user's announcements.
     * This list gets displayed on the admin page which is only accessible by admins.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @return a list of the latest user's announcements
     */
    @GetMapping("/announcements")
    public List<Post> getLatestUsersAnnouncements(@CurrentSecurityContext(expression="authentication") Authentication authentication) {
        String email = authentication.getName();
        return postService.getUsersAnnouncements(email);
    }

    /***
     * Returns a list of the latest posts. This includes user's posts, their friends' posts, and
     * announcements created by all administrators. In total, this list doesn't exceed 10 records.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @return a list of the latest posts
     */
    @GetMapping
    public ResponseEntity<?> getAllPosts(@CurrentSecurityContext(expression="authentication") Authentication authentication) {
        // Get the session user.
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();

        // Create a map which we will return as the body of the response. This map
        // holds all the latest posts as well as the session user.
        Map<String, Object> body = new HashMap<>();
        body.put("posts", postService.getMainPagePosts(sessionUser));
        body.put("user", sessionUser);

        // Send a response message OK
        return new ResponseEntity(body, HttpStatus.OK);
    }

    /***
     * Likes a post. The user cannot like their own posts nor the posts that they have previously liked.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param id the id of the post that is going to be liked by the session user
     */
    @PostMapping("/{id}/likes")
    public void likePost(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                         @PathVariable long id) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        postService.likePost(id, sessionUser);
    }

    /***
     * Unlikes a posts. The user can unlike only the posts that they have previously liked.
     * @param authentication an instance of Authentication through which we get the session user (who sent the request)
     * @param id the id of the post that is going to be unliked by the session user
     */
    @DeleteMapping("/{id}/likes")
    public void unlikePost(@CurrentSecurityContext(expression = "authentication") Authentication authentication,
                           @PathVariable long id) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        postService.unlikePost(id, sessionUser);
    }
}
