package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.dob;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.dob.DobConstants.MINIMAL_AGE_FOR_REGISTRATION_REQUIRED;
import static java.time.LocalDate.now;

public class SecureDobValidator implements ConstraintValidator<SecureDobConstraint, LocalDate> {

    @Override
    public void initialize(SecureDobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext constraintValidatorContext) {
        return dob.isBefore(now().minusYears(MINIMAL_AGE_FOR_REGISTRATION_REQUIRED));
    }
}
