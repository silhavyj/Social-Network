package cz.zcu.kiv.pia.silhavyj.socialnetwork;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.config.AppConfiguration;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IRoleService;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.Stream;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.USER;

@SpringBootApplication
@RequiredArgsConstructor
public class SocialNetworkApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(SocialNetworkApplication.class);

	private final AppConfiguration appConfiguration;
	private final IUserService userService;
	private final IRoleService roleService;

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (roleService.getRowCount() == 0) {
			addDefaultRolesToDatabase();
			addDefaultUsersToDatabase();
		}
	}

	private void addDefaultRolesToDatabase() {
		roleService.createAllRoles();
	}

	private void addDefaultUsersToDatabase() {
		appConfiguration.getUsers().stream()
				.forEach(defaultUserConfig -> {
					User user = defaultUserConfig.buildUser();
					setUserRole(user, defaultUserConfig.getRole());
					logger.info(user.toString());
					userService.encryptUserPassword(user);
					userService.saveUser(user);
				});
	}

	private void setUserRole(User user, String userRoleStr) {
		Role role = roleService.getRoleByUserRole(USER).orElseThrow(() -> new RuntimeException("role USER not found in the database"));
		user.getRoles().add(role);

		UserRole userRole = Stream.of(UserRole.values())
				.filter(name -> name.name().equals(userRoleStr))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Invalid use of role in application.yml"));

		role = roleService.getRoleByUserRole(userRole).orElseThrow(() -> new RuntimeException("role " + userRoleStr + " not found in the database"));
		user.getRoles().add(role);
	}
}
