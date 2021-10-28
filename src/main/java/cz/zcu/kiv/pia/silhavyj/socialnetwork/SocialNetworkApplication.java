package cz.zcu.kiv.pia.silhavyj.socialnetwork;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class SocialNetworkApplication implements CommandLineRunner {

	private final AppConfiguration appConfiguration;
	private final IUserService userService;

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		addDefaultUsersToDatabase();
	}

	private void addDefaultUsersToDatabase() {
		// add default users into the database
		// the default users are listed out in application.yml
		appConfiguration.getUsers().stream()
				.forEach(defaultUserConfig -> {
					User user = defaultUserConfig.buildUser(); // create User
					user.setLocked(true);
					userService.encryptUserPassword(user);	   // encrypt their password
					userService.saveUser(user);				   // store them in the database
				});
	}
}
