package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.registration;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.exceptions.ResetPasswordEmailNotFoundException;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserConstants.RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG;

@Service
@RequiredArgsConstructor
public class RegistrationService implements IRegistrationService {

    private final IUserService userService;

    @Override
    public void sendTokenForResettingPassword(String email) {
        User user = userService.getUserByEmail(email).orElseThrow(() ->
                new ResetPasswordEmailNotFoundException(RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG));
    }

    @Override
    public void signUpUser(User user) {
        // TODO
    }
}
