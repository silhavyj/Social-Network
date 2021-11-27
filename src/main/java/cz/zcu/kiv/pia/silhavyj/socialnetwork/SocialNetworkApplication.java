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

/***
 * The main class of the application
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@SpringBootApplication
@RequiredArgsConstructor
public class SocialNetworkApplication implements CommandLineRunner {

	/*** logger used to log errors when inserting data into the database (at the very beginning) */
	private static final Logger logger = LoggerFactory.getLogger(SocialNetworkApplication.class);

	/*** instance of AppConfiguration (default users) */
	private final AppConfiguration appConfiguration;

	/*** implementation of IUserService (storing default users into the database) */
	private final IUserService userService;

	/*** implementation of IRoleService (storing default user roles into the database) */
	private final IRoleService roleService;

	/***
	 * Main entry point of the application
	 * @param args unused
	 */
	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

	/***
	 * Init method called after the application starts
	 * @param args unused
	 * @throws Exception
	 */
	@Override
	public void run(String... args) throws Exception {
		// Check if there are some data in the database
		// and if not, initialize the database - default user, user roles
		if (roleService.getRowCount() == 0) {
			addDefaultRolesToDatabase();
			addDefaultUsersToDatabase();
		}
	}

	/***
	 * Adds user roles into the database
	 */
	private void addDefaultRolesToDatabase() {
		roleService.createAllRoles();
	}

	/***
	 * Inserts default users into the database
	 */
	private void addDefaultUsersToDatabase() {
		appConfiguration.getUsers().stream()
				.forEach(defaultUserConfig -> {
					User user = defaultUserConfig.buildUser();       // build the user
					setUserRole(user, defaultUserConfig.getRole());  // assign the user their role
					logger.info(user.toString());                    // log the user
					userService.encryptUserPassword(user);           // encrypt their password
					userService.saveUser(user);                      // save the user into the database
				});
	}

	/***
	 * Assigns a user their role
	 * @param user instance of User
	 * @param userRoleStr type of role they are going to be assigned
	 */
	private void setUserRole(User user, String userRoleStr) {
		// Every user is assigned the USER role
		Role role = roleService.getRoleByUserRole(USER).orElseThrow(() -> new RuntimeException("role USER not found in the database"));
		user.getRoles().add(role);

		// Find the possible additional role in the database by its name passed as a parameter.
		// If it's not found, throw an exception
		UserRole userRole = Stream.of(UserRole.values())
				.filter(name -> name.name().equals(userRoleStr))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Invalid use of role in application.yml"));

		// Assign the user their role
		role = roleService.getRoleByUserRole(userRole).orElseThrow(() -> new RuntimeException("role " + userRoleStr + " not found in the database"));
		user.getRoles().add(role);
	}
}
