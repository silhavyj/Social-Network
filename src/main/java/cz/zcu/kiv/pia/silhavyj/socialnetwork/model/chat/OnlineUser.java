package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.chat;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OnlineUser {

    private String fullName;
    private String email;
    private OnlineUserStatus onlineUserStatus;
    private String profilePicturePath;
}
