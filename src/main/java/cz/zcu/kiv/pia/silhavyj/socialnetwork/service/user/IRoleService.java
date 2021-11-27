package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole;

import java.util.Optional;

/***
 * Method definitions of a role service.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public interface IRoleService {

    /***
     * Creates all user roles. This method is called when the database schema
     * is being created at the very beginning. After that, it's never called again.
     */
    void createAllRoles();

    /***
     * Returns role by a user role (enumeration USER, ADMIN, ...)
     * @param userRole type of role
     * @return role by a user role (enumeration USER, ADMIN, ...)
     */
    Optional<Role> getRoleByUserRole(UserRole userRole);

    /***
     * Returns the number of row of the table in the database.
     * This method is used as an indication that the table needs
     * to be initialized (at the very beginning - row count = 0).
     * @return number of rows of the table in the database
     */
    long getRowCount();
}
