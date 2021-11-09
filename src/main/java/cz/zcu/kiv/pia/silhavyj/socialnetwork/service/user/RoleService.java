package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.user;

import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.Role;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;

    @Override
    public void createAllRoles() {
        Stream.of(UserRole.values())
              .forEach(userRole -> roleRepository.save(new Role(userRole)));
    }

    @Override
    public Optional<Role> getRoleByUserRole(UserRole userRole) {
        return roleRepository.getRoleByUserRole(userRole);
    }
}
