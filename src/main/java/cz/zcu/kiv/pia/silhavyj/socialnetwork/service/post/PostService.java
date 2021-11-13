package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.post;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IPostRepository;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.friendship.IFriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.ANNOUNCEMENT;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.NORMAL_POST;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final IPostRepository postRepository;
    private final AppConfiguration appConfiguration;
    private final IFriendshipService friendshipService;

    @Override
    public void createPost(User user, String message) {
        Post post = new Post(user, message, NORMAL_POST);
        postRepository.save(post);
    }

    @Override
    public List<Post> getUsersPosts(String userEmail) {
        return postRepository.findTopUsersPosts(userEmail, PageRequest.of(0, appConfiguration.getPostsToDisplayOnProfilePage()));
    }

    @Override
    public List<Post> getMainPagePosts(User user) {
        var friends = friendshipService.getAllAcceptedFriends(user);
        List<Post> posts = getUsersPosts(user.getEmail());
        for (var friend : friends) {
            posts.addAll(getUsersPosts(friend.getEmail()));
        }
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPostedAt).reversed())
                .limit(appConfiguration.getPostsToDisplayOnProfilePage())
                .collect(Collectors.toList());
    }

    @Override
    public void createAnnouncement(User user, String message) {
        Post post = new Post(user, message, ANNOUNCEMENT);
        postRepository.save(post);
    }
}
