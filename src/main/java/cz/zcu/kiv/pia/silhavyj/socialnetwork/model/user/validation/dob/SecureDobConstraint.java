package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.dob;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.dob.DobConstants.MINIMAL_AGE_FOR_REGISTRATION_REQUIRED;

@Documented
@Constraint(validatedBy = SecureDobValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureDobConstraint {

    String message() default "You must be at least " + MINIMAL_AGE_FOR_REGISTRATION_REQUIRED + " years old";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
