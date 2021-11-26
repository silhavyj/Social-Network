package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.dob.SecureDobConstraint;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password.SecurePasswordConstraint;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.UserConstants.*;
import static javax.persistence.GenerationType.IDENTITY;

/***
 * This class represents a User registered within the system. It implements
 * UserDetails, so the process of signing up can be done automatically by
 * Spring Boot. It holds all information that are kept about a user such as
 * their name, e-mail (unique), date of birth, password, etc.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email"})
@ToString
public class User implements UserDetails {

    /*** primary key of the users table within the database */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /*** user's firstname */
    @NotEmpty(message = FIRSTNAME_EMPTY_ERR_MSG)
    @Size(min = FIRSTNAME_MIN_LENGTH, max = FIRSTNAME_MAX_LENGTH, message = FIRSTNAME_INVALID_LENGTH_ERR_MSG)
    private String firstname;

    /*** user's lastname */
    @NotEmpty(message = LASTNAME_EMPTY_ERR_MSG)
    @Size(min = LASTNAME_MIN_LENGTH, max = LASTNAME_MAX_LENGTH, message = LASTNAME_INVALID_LENGTH_ERR_MSG)
    private String lastname;

    /*** user's password */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @SecurePasswordConstraint(message = INSECURE_PASSWORD_ERR_MSG)
    private String password;

    /*** user's date of birth */
    @NotNull(message = DOB_EMPTY_ERR_MSG)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @SecureDobConstraint
    private LocalDate dob;

    /*** user's e-mail address */
    @NotEmpty(message = EMAIL_EMPTY_ERR_MSG)
    @Email(message = EMAIL_INVALID_ERR_MSG)
    @Column(unique = true)
    private String email;

    /*** time stamp when the user was created */
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /*** user's gender (UNDEFINED by default) */
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNDEFINED;

    /*** flag if the account is locked */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean locked = false;

    /*** flag if the account is enabled */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean enabled = false;

    /*** set of roles that the user was assigned */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    /*** path to the user's profile picture */
    private String profilePicturePath = DEFAULT_PROFILE_IMAGE_PATH;

    /***
     * Creates a collection of GrantedAuthorities that the user has been assigned.
     * Basically, it goes of the set of roles and out of each role it creates an instance of SimpleGrantedAuthority.
     * These granted authorities are used when restricting access to certain end-points of the application.
     * @return collection of GrantedAuthorities
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Create a lif of SimpleGrantedAuthority
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Create an instance of SimpleGrantedAuthority for every
        // single role that the user has been assigned.
        for (var role : roles) {
            authorities.add(new SimpleGrantedAuthority(("ROLE_" + role.getUserRole().name())));
        }

        // return the collection of GrantedAuthority
        return authorities;
    }

    /***
     * Returns user's password
     * @return user's password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /***
     * Returns user's username (their e-mail address)
     * @return user's e-mail address
     */
    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    /***
     * Returns information whether the account is expired or not
     * @return within this project it always returns true (not expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /***
     * Returns information whether the account is locked or not
     * @return there is no option to block accounts within this project,
     *         so it always returns false (account isn't locked)
     */
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    /***
     * Returns information whether the account credentials are non-expired
     * @return within this project, it always returns true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /***
     * Return information whether the account is enabled or not.
     * After signing-up the user is required to confirm their registration via e-mail
     * in order to enable (activate) their account.
     * @return true, if the account is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /***
     * Return user's full name
     * @return firstname with lastname separated by a space
     */
    public String getFullName() {
        return getFirstname() + " " + getLastname();
    }
}
