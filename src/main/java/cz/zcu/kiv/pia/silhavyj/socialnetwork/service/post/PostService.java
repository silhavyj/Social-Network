package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.post;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.NORMAL_POST;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final IPostRepository postRepository;
    private final AppConfiguration appConfiguration;

    @Override
    public void createPost(User sessionUser, String message) {
        Post post = new Post(sessionUser, message, NORMAL_POST);
        postRepository.save(post);
    }

    @Override
    public List<Post> getUsersPosts(User sessionUser) {
        return postRepository.findTopUsersPosts(sessionUser.getEmail(), PageRequest.of(0, appConfiguration.getPostsToDisplayOnProfilePage()));
    }
}
