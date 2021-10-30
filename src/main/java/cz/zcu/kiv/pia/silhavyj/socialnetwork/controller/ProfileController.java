package cz.zcu.kiv.pia.silhavyj.socialnetwork.controller;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.image.ValidImage;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.ProfileConstants.*;

@Controller
@Validated
@RequiredArgsConstructor
public class ProfileController {

    private final IUserService userService;


    @GetMapping("/profile")
    public String getProfile(@CurrentSecurityContext(expression="authentication") Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        model.addAttribute("session_user", user);
        return "profile";
    }

    @PostMapping("/profile/change-profile-picture")
    public String changeProfilePicture(@RequestParam("profilePicture") @ValidImage MultipartFile profilePicture,
                                       @CurrentSecurityContext(expression="authentication") Authentication authentication,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).get();
        userService.updateProfilePicture(user, profilePicture);

        model.addAttribute("session_user", user);
        redirectAttributes.addFlashAttribute(PROFILE_PERSONAL_INFO_SUCCESS_MSG_NAME, PROFILE_INFO_UPDATED_INTO_MSG);
        return "redirect:/profile";
    }
}
