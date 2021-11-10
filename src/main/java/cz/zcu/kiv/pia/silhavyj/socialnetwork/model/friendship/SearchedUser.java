package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.friendship;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchedUser implements Serializable {

    private String profilePicturePath;
    private String firstname;
    private String lastname;
    private String email;
    private FriendRequestStatus status;

    public SearchedUser(final User user, FriendRequestStatus status) {
        this.profilePicturePath = user.getProfilePicturePath();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.status = status;
    }
}
