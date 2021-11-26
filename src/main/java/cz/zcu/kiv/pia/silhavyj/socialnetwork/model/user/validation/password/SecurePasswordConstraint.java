package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/***
 * Custom constraint used when validating user's password.
 * When the user wants to sign up, or change their password, they're
 * not allowed to use very weak or weak passwords.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Documented
@Constraint(validatedBy = SecurePasswordValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurePasswordConstraint {

    /*** default error message returned to the user (front end) */
    String message() default "password validation error";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
