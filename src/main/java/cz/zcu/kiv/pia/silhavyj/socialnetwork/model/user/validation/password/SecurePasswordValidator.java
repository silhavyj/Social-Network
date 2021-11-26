package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.constant.PasswordConstants.*;
import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password.PasswordEntropyClass.*;

/***
 * Custom password validator. This class overrides a method that takes
 * an instance of String as a parameter and returns true if the password
 * falls into category REASONABLE or higher. Otherwise, it returns false.
 *
 * https://www.pleacher.com/mp/mlessons/algebra/entropy.html
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Component
public class SecurePasswordValidator implements ConstraintValidator<SecurePasswordConstraint, String> {

    /***
     * Initializes SecurePasswordValidator
     * @param constraintAnnotation parameter passed to ConstraintValidator.super
     */
    @Override
    public void initialize(SecurePasswordConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /***
     * Validates user's password (assesses its strength)
     * @param password plain-text password
     * @param constraintValidatorContext not used in this case
     * @return Returns true if the password strength falls into the REASONABLE category or higher. Otherwise, returns false.
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        // Calculate the entropy of the password
        long passwordEntropy = calculatePasswordEntropy(password);

        // Check if it's at least REASONABLE
        return getPasswordEntropyClass(passwordEntropy).ordinal() >= REASONABLE.ordinal();
    }

    /***
     * Returns a password strength category based on its entropy value.
     * @param entropy calculated entropy of the password
     * @return corresponding category the password falls into
     */
    public PasswordEntropyClass getPasswordEntropyClass(long entropy) {
        if (entropy < 28) {
            return VERY_WEAK;
        } else if (entropy >= 28 && entropy <= 35) {
            return WEAK;
        } else if (entropy >= 36 && entropy <= 59) {
            return REASONABLE;
        } else if (entropy >= 60 && entropy <= 127) {
            return STRONG;
        }
        return VERY_STRONG;
    }

    /***
     * Calculates the entropy of a password by the algorithm described over at https://www.pleacher.com/mp/mlessons/algebra/entropy.html
     * @param password password that is going to be evaluated
     * @return entropy value of the password passed in as a parameter
     */
    public long calculatePasswordEntropy(String password) {
        // Make sure the password is not null
        if (password == null) {
            return 0;
        }

        // Calculate the number of special characters in the password.
        final long specialCharacters = password.chars().filter(c ->
                        !Character.isDigit(c)  &&
                        !Character.isLetter(c) &&
                        !Character.isSpaceChar(c)).count();

        // Calculate the number of uppercase characters in the password.
        final long upperCase = password.chars().filter(c -> Character.isUpperCase(c)).count();

        // Calculate the number of lowercase characters in the password.
        final long lowerCaseLetters = password.chars().filter(c -> Character.isLowerCase(c)).count();

        // Calculate the number of digits in the password.
        final long digitCharacter = password.chars().filter(c -> Character.isDigit(c)).count();

        // Calculate the pool size by the number of different types of
        // characters found in the password.
        long poolSize = 0;
        if (lowerCaseLetters > 0) {
            poolSize += LOWERCASE_POOL_SIZE;
        }
        if (upperCase > 0) {
            poolSize += UPPERCASE_POOL_SIZE;
        }
        if (digitCharacter > 0) {
            poolSize += DIGIT_POOL_SIZE;
        }
        if (specialCharacters > 0) {
            poolSize += SPECIAL_CHARACTERS_POOL_SIZE;
        }

        // Calculate the entropy of the password
        long entropy = (long)(Math.log(Math.pow(poolSize, password.length())) / Math.log(2));
        return entropy;
    }
}
