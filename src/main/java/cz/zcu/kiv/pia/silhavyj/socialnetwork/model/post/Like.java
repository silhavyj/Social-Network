package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.post;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private LocalDateTime likedAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Like(User user) {
        this.user = user;
        this.likedAt = now();
    }
}
