package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static javax.persistence.GenerationType.IDENTITY;

/***
 * This class represents a like that can be "attached" to a post.
 * Every registered user can like any posts but the ones they have posted
 * themselves or the ones they have already liked.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "likes")
public class Like {

    /*** primary key of the likes table within the database */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /*** time stamp when the user liked the post */
    private LocalDateTime likedAt;

    /*** user who liked the post */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /***
     * Creates an instance of Like
     * @param user instance of User who liked the post
     */
    public Like(User user) {
        this.user = user;
        this.likedAt = now();
    }
}
