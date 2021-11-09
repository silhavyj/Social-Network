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

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email"})
@ToString
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotEmpty(message = FIRSTNAME_EMPTY_ERR_MSG)
    @Size(min = FIRSTNAME_MIN_LENGTH, max = FIRSTNAME_MAX_LENGTH, message = FIRSTNAME_INVALID_LENGTH_ERR_MSG)
    private String firstname;

    @NotEmpty(message = LASTNAME_EMPTY_ERR_MSG)
    @Size(min = LASTNAME_MIN_LENGTH, max = LASTNAME_MAX_LENGTH, message = LASTNAME_INVALID_LENGTH_ERR_MSG)
    private String lastname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @SecurePasswordConstraint(message = INSECURE_PASSWORD_ERR_MSG)
    private String password;

    @NotNull(message = DOB_EMPTY_ERR_MSG)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @SecureDobConstraint
    private LocalDate dob;

    @NotEmpty(message = EMAIL_EMPTY_ERR_MSG)
    @Email(message = EMAIL_INVALID_ERR_MSG)
    @Column(unique = true)
    private String email;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNDEFINED;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean locked = false;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean enabled = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    private String profilePicturePath = DEFAULT_PROFILE_IMAGE_PATH;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (var role : roles)
            authorities.add(new SimpleGrantedAuthority(("ROLE_" + role.getUserRole().name())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getFullName() {
        return getFirstname() + " " + getLastname();
    }
}
