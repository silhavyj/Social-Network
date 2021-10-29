package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

import java.util.Optional;

public interface IUserService {

    void encryptUserPassword(User user);
    void saveUser(User user);
    Optional<User> getUserByEmail(String email);
    void deleteUserByEmail(String email);
}
