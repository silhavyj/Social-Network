package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.post;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

import java.util.List;

public interface IPostService {

    void createPost(User sessionUser, String message);
    List<Post> getUsersPosts(User sessionUser);
}
