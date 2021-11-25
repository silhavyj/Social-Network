package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.Data;

import java.io.Serializable;

/***
 * Data structure returned to the user when searching friends.
 * Basically, it contains less information about the user (just the necessary stuff)
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Data
public class SearchedUser implements Serializable {

    /*** path of the profile picture of the user */
    private String profilePicturePath;

    /*** user's first name */
    private String firstname;

    /*** user's lastname */
    private String lastname;

    /*** user's e-mail address */
    private String email;

    /*** relation status the searched user has to the searching user */
    private FriendRequestStatus status;

    /***
     * Creates an instance of SearchedUser
     * @param user instance of User (we just store the necessary information off of it)
     * @param status relation status the searched user has to the searching user
     */
    public SearchedUser(final User user, FriendRequestStatus status) {
        this.profilePicturePath = user.getProfilePicturePath();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.status = status;
    }
}
