package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;

/***
 * This class handles all the logic related to user roles. For instance,
 * creating all roles at the very beginning, retrieving roles from
 * the database, etc.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoleService implements IRoleService {

    /*** implementation of IRoleRepository (retrieving user roles from the database) */
    private final IRoleRepository roleRepository;

    /***
     * Creates all user roles. This method is called when the database schema
     * is being created at the very beginning. After that, it's never called again.
     */
    @Override
    public void createAllRoles() {
        Stream.of(UserRole.values())
              .forEach(userRole -> roleRepository.save(new Role(userRole)));
    }

    /***
     * Returns role by a user role (enumeration USER, ADMIN, ...)
     * @param userRole type of role
     * @return role by a user role (enumeration USER, ADMIN, ...)
     */
    @Override
    public Optional<Role> getRoleByUserRole(UserRole userRole) {
        return roleRepository.getRoleByUserRole(userRole);
    }

    /***
     * Returns the number of row of the table in the database.
     * This method is used as an indication that the table needs
     * to be initialized (at the very beginning - row count = 0).
     * @return number of rows of the table in the database
     */
    @Override
    public long getRowCount() {
        return roleRepository.count();
    }
}
