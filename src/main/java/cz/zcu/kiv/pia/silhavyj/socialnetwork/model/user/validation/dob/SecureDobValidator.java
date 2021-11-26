package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.dob;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.DobConstants.MINIMAL_AGE_FOR_REGISTRATION_REQUIRED;
import static java.time.LocalDate.now;

/***
 * Custom date of birth validator. This class overrides a method that takes
 * an instance of LocalDate as a parameter and returns true or false
 * depending on whether the condition of being at least 15 y.o. is satisfied or not.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Component
public class SecureDobValidator implements ConstraintValidator<SecureDobConstraint, LocalDate> {

    /***
     * Initializes SecureDobValidator
     * @param constraintAnnotation parameter passed to ConstraintValidator.super
     */
    @Override
    public void initialize(SecureDobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /***
     * Return true if the user is at least 15 years old. Otherwise, returns false.
     * @param dob instance of LocalDate (user's birthday)
     * @param constraintValidatorContext not used in this case
     * @return true/false depending on whether the condition is satisfied or not
     */
    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext constraintValidatorContext) {
        return dob != null && dob.isBefore(now().minusYears(MINIMAL_AGE_FOR_REGISTRATION_REQUIRED));
    }
}
