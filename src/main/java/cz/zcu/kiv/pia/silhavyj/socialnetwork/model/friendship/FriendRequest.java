package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/***
 * This class represents a friend request sent from
 * one user to another with the intention of becoming friends.
 * The receiver of the friend request has three options as to what to do with it.
 * They can either accept the friend request, they can reject the friend request, or
 * the can block the sending user preventing themselves of receiving another
 * friend request from the same user.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friend_request")
public class FriendRequest {

    /*** primary key of the friend_request table within the database */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*** status of the friend request */
    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    /*** user who sent the friend request (sender) */
    @ManyToOne
    @JoinColumn(name = "user_sender")
    private User requestSender;

    /*** user who received the friend request (receiver) */
    @ManyToOne
    @JoinColumn(name = "user_receiver")
    private User requestReceiver;
}
