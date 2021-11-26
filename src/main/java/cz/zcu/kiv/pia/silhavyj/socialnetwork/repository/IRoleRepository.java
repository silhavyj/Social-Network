package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/***
 * JPA repository that handles SQL queries regarding user roles.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    /***
     * Returns a role by user role (enumeration)
     * @param userRole type of user role we want to find (ADMIN, USER, ...)
     * @return matching Role (Optional - can be null)
     */
    Optional<Role> getRoleByUserRole(UserRole userRole);
}
