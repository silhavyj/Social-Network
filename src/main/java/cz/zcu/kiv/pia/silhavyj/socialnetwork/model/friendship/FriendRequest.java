package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friend_request")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    @ManyToOne
    @JoinColumn(name = "user_sender")
    private User requestSender;

    @ManyToOne
    @JoinColumn(name = "user_receiver")
    private User requestReceiver;
}
