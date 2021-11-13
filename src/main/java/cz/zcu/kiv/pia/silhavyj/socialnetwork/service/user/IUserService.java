package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    void encryptUserPassword(User user);
    void saveUser(User user);
    Optional<User> getUserByEmail(String email);
    void updateProfilePicture(User user, MultipartFile profilePicture);
    boolean isValidProfilePicture(MultipartFile multipartFile);
    boolean matchesUserPassword(User user, String oldPassword);
    void setUserPassword(User user, String newPassword);
    boolean isSecurePassword(String password);
    void updatePersonalInfo(User newUser, User oldUser);
    List<User> searchUsers(String name, String sessionUserEmail);
    void escalateToAdmin(String email);
    void removeAdminPrivileges(String email);
}
