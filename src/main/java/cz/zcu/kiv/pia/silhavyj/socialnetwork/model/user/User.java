package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.dob.SecureDobConstraint;
import cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password.SecurePasswordConstraint;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.UserRole.USER;
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

    @NotEmpty(message = "Firstname cannot be empty")
    @Size(min = 2, max = 255, message = "Firstname length should be between 2 and 255")
    private String firstname;

    @NotEmpty(message = "Lastname cannot be empty")
    @Size(min = 2, max = 255, message = "Lastname length should be between 2 and 255")
    private String lastname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @SecurePasswordConstraint(message = "Password is not secure enough")
    private String password;

    @NotNull(message = "Date of birth cannot be empty")
    @SecureDobConstraint
    private LocalDate dob;

    @NotEmpty(message = "E-mail cannot be empty")
    @Email(message = "E-mail is not valid")
    @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    private UserRole userRole = USER;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNDEFINED;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean locked = false;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean enabled = false;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + userRole.name());
        return Collections.singletonList(simpleGrantedAuthority);
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
