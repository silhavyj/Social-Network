package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.registration;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;

public interface IRegistrationService {

    void sendTokenForResettingPassword(String email);
    void signUpUser(User user);
    void activateUserAccount(String token);
}
