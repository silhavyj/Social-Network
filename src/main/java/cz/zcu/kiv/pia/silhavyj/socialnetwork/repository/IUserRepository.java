package cz.zcu.kiv.pia.silhavyj.socialnetwork.repository;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/***
 * JPA repository that handles SQL queries regarding users.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    /***
     * Returns a user found by their e-mail address
     * @param email user's e-mail address
     * @return matching user (Optional - can be null)
     */
    Optional<User> findByEmail(String email);

    /***
     * Returns a list of users found by either their firstname or lastname
     * @param name a sequence of characters provided by the user when searching users
     * @param sessionUserEmail e-mail address of the session user
     * @return list of matching users
     */
    @Query("SELECT user FROM User user WHERE user.email <> ?2 AND (user.firstname LIKE %?1% OR user.lastname LIKE %?1%)")
    List<User> searchUsers(String name, String sessionUserEmail);

    /***
     * Returns users with their roles being the collection provided as a parameter
     * @param roles desired collection of user roles
     * @return list of users with matching user roles
     */
    List<User> findByRolesIn(Collection<Role> roles);
}
