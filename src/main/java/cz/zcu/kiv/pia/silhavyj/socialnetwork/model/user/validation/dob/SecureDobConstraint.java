package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.dob;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.DobConstants.MINIMAL_AGE_FOR_REGISTRATION_REQUIRED;

/***
 * Custom constraint used when validating user's birthday.
 * When the user wants to sign up, they must be at least 15 years old.
 * If they do not satisfy this condition, they'll be presented with an
 * error message.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Documented
@Constraint(validatedBy = SecureDobValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureDobConstraint {

    /*** default error message returned to the user (front end) */
    String message() default "You must be at least " + MINIMAL_AGE_FOR_REGISTRATION_REQUIRED + " years old";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
