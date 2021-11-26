package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/***
 * This class represents a user role as a table in the database.
 * The relation between a user and a role is, as required, N:M.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Entity
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "userRole")
@Getter
@Setter
@Table(name = "roles")
public class Role {

    /*** primary key of the roles table within the database */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /*** type of the user role (admin, user, ...) */
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private UserRole userRole;

    /***
     * Creates an instance of Role
     * @param userRole type of the user role (admin, user, ...)
     */
    public Role(UserRole userRole) {
        this.userRole = userRole;
    }
}
