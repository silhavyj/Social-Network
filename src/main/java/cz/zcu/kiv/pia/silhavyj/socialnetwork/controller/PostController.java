package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.post.IPostService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void addAnnouncements() {
        // TODO
    }

    @GetMapping
    public List<Post> getAllPosts(@CurrentSecurityContext(expression="authentication") Authentication authentication) {
        String email = authentication.getName();
        User sessionUser = userService.getUserByEmail(email).get();
        return postService.getMainPagePosts(sessionUser);
    }
}
