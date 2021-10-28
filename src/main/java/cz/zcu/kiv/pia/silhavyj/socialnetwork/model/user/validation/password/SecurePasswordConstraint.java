package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SecurePasswordValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurePasswordConstraint {

    String message() default "password validation error";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
