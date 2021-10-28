package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password.PasswordConstants.*;

@Service
public class SecurePasswordValidator implements ConstraintValidator<SecurePasswordConstraint, String> {

    @Override
    public void initialize(SecurePasswordConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null)
            return false;

        final long specialCharacters = password.chars().filter(c ->
                                                              !Character.isDigit(c)  &&
                                                              !Character.isLetter(c) &&
                                                              !Character.isSpaceChar(c)).count();

        final long capitalLetters = password.chars().filter(c -> Character.isUpperCase(c)).count();
        final long lowerCaseLetters = password.chars().filter(c -> Character.isLowerCase(c)).count();
        final long digitCharacter = password.chars().filter(c -> Character.isDigit(c)).count();

        return password.length() >= MINIMAL_PASSWORD_LENGTH                         &&
               specialCharacters >= MINIMAL_NUMBER_OF_SPECIAL_CHARACTERS_REQUIRED   &&
               capitalLetters    >= MINIMAL_NUMBER_OF_UPPERCASE_CHARACTERS_REQUIRED &&
               lowerCaseLetters  >= MINIMAL_NUMBER_OF_LOWERCASE_CHARACTERS_REQUIRED &&
               digitCharacter    >= MINIMAL_NUMBER_OF_DIGIT_CHARACTERS_REQUIRED;
    }
}
