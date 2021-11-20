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

@Service
@Transactional
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final IPostRepository postRepository;
    private final AppConfiguration appConfiguration;
    private final IFriendshipService friendshipService;
    private final IUserService userService;
    private final ILikeRepository likeRepository;

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
        List<User> admins = userService.getAdmins();
        for (var admin : admins) {
            posts.addAll(getUsersAnnouncements(admin.getEmail()));
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

    @Override
    public List<Post> getUsersAnnouncements(String userEmail) {
        return postRepository.findTopUsersAnnouncements(userEmail, PageRequest.of(0, appConfiguration.getPostsToDisplayOnProfilePage()));
    }

    @Override
    public void likePost(long postId, User sessionUser) {
        Optional<Post> post = Optional.of(postRepository.getById(postId));
        if (post.isEmpty())
            return;

        boolean alreadyLiked = post.get().getLikes()
                .stream()
                .filter(like -> like.getUser().getEmail().equals(sessionUser.getEmail()))
                .findFirst()
                .isPresent();

        if (alreadyLiked == true || post.get().getUser().getEmail().equals(sessionUser.getEmail()))
            return;

        post.get().getLikes().add(new Like(sessionUser));
        postRepository.save(post.get());
    }

    @Override
    public void unlikePost(long postId, User sessionUser) {
        Optional<Post> post = Optional.of(postRepository.getById(postId));
        if (post.isEmpty())
            return;

        Optional<Like> likeToDelete = post.get().getLikes()
                        .stream()
                        .filter(like -> like.getUser().getEmail().equals(sessionUser.getEmail()))
                        .findFirst();

        if (likeToDelete.isPresent()) {
            post.get().getLikes().removeIf(like -> like.getUser().getEmail().equals(sessionUser.getEmail()));
            postRepository.save(post.get());
            likeRepository.delete(likeToDelete.get());
        }
    }
}
