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

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;
    private final IUserService userService;

    @PostMapping("/normal-posts")
    public void addOrdinaryPost(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                @RequestBody String message) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        postService.createPost(sessionUser, message);
    }

    @GetMapping("/normal-posts")
    public List<Post> getLatestUsersPosts(@CurrentSecurityContext(expression="authentication") Authentication authentication) {
        String email = authentication.getName();
        return postService.getUsersPosts(email);
    }

    @PostMapping("/announcements")
    public void addAnnouncements(@CurrentSecurityContext(expression="authentication") Authentication authentication,
                                 @RequestBody String message) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        postService.createAnnouncement(sessionUser, message);
    }

    @GetMapping("/announcements")
    public List<Post> getLatestUsersAnnouncements(@CurrentSecurityContext(expression="authentication") Authentication authentication) {
        String email = authentication.getName();
        return postService.getUsersAnnouncements(email);
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(@CurrentSecurityContext(expression="authentication") Authentication authentication) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        Map<String, Object> body = new HashMap<>();
        body.put("posts", postService.getMainPagePosts(sessionUser));
        body.put("user", sessionUser);
        return new ResponseEntity(body, HttpStatus.OK);
    }

    @PostMapping("/{id}/likes")
    public void likePost(@CurrentSecurityContext(expression = "authentication") Authentication authentication, @PathVariable long id) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        postService.likePost(id, sessionUser);
    }

    @DeleteMapping("/{id}/likes")
    public void unlikePost(@CurrentSecurityContext(expression = "authentication") Authentication authentication, @PathVariable long id) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        postService.unlikePost(id, sessionUser);
    }
}
