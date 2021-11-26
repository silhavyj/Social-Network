package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

/***
 * This class represents a post or an announcement (different type of post).
 * Any user can create a post. Their posts will be seen by their friends and not
 * anyone else. Announcements can be created only by the administrator and are
 * visible to anyone regardless of their friendship.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {

    /*** primary key of the posts table within the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*** user who created the posts (or announcement) */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    /*** type of the post */
    @Enumerated(EnumType.STRING)
    private PostType postType;

    /*** plain text content of the post */
    @Column(columnDefinition="TEXT")
    String content;

    /*** time stamp when the post was created */
    private LocalDateTime postedAt;

    /*** all likes of the post */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<Like> likes;

    /***
     * Creates an instance of Post
     * @param user instance of User who created the post
     * @param content content (plain text) of the post
     * @param postType type of the post
     */
    public Post(User user, String content, PostType postType) {
        this.content = content;
        this.user = user;
        this.postType = postType;
        this.postedAt = now();
    }
}
