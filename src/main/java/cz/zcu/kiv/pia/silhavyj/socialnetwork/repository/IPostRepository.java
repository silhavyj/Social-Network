package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/***
 * JPA repository that handles SQL queries regarding posts.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {

    /***
     * Returns top latest user's post. These posts are then displayed on the user's profile page.
     * @param userEmail e-mail address of the user
     * @param pageable how many posts we want to retrieve from the database
     * @return list of latest user's post
     */
    @Query("SELECT post from Post post WHERE post.user.email = ?1 AND post.postType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.NORMAL_POST ORDER BY post.postedAt DESC")
    List<Post> findTopUsersPosts(String userEmail, Pageable pageable);

    /***
     * Returns top latest user's announcements. These posts are then displayed on the user's administrator page.
     * @param userEmail e-mail address of the user
     * @param pageable how many announcements we want to retrieve from the database
     * @return list of latest user's announcements
     */
    @Query("SELECT post from Post post WHERE post.user.email = ?1 AND post.postType = cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post.PostType.ANNOUNCEMENT ORDER BY post.postedAt DESC")
    List<Post> findTopUsersAnnouncements(String userEmail, PageRequest pageable);
}
