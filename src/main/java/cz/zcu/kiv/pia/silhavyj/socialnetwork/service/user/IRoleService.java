package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole;

import java.util.Optional;

public interface IRoleService {

    void createAllRoles();
    Optional<Role> getRoleByUserRole(UserRole userRole);
    long getRowCount();
}
