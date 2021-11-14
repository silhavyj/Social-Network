package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT post from Post post WHERE post.user.email = ?1 AND post.postType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.NORMAL_POST ORDER BY post.postedAt DESC")
    List<Post> findTopUsersPosts(String userEmail, Pageable pageable);

    @Query("SELECT post from Post post WHERE post.user.email = ?1 AND post.postType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.ANNOUNCEMENT ORDER BY post.postedAt DESC")
    List<Post> findTopUsersAnnouncements(String userEmail, PageRequest pageable);
}
