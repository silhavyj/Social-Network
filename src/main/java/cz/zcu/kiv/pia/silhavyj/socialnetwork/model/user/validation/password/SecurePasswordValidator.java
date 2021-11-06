package cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password.PasswordEntropyClass.*;

@Service
public class SecurePasswordValidator implements ConstraintValidator<SecurePasswordConstraint, String> {

    // https://www.pleacher.com/mp/mlessons/algebra/entropy.html
    private static int LOWERCASE_POOL_SIZE = 26;
    private static int UPPERCASE_POOL_SIZE = 26;
    private static int DIGIT_POOL_SIZE = 10;
    private static int SPECIAL_CHARACTERS_POOL_SIZE = 33;

    @Override
    public void initialize(SecurePasswordConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    public PasswordEntropyClass getPasswordEntropyClass(long entropy) {
        if (entropy < 28)
            return VERY_WEAK;
        if (entropy >= 28 && entropy <= 35)
            return WEAK;
        if (entropy >= 36 && entropy <= 59)
            return REASONABLE;
        if (entropy >= 60 && entropy <= 127)
            return STRONG;
        return VERY_STRONG;
    }

    public long calculatePasswordEntropy(String password) {
        if (password == null)
            return 0;

        final long specialCharacters = password.chars().filter(c ->
                !Character.isDigit(c)  &&
                        !Character.isLetter(c) &&
                        !Character.isSpaceChar(c)).count();

        final long upperCase = password.chars().filter(c -> Character.isUpperCase(c)).count();
        final long lowerCaseLetters = password.chars().filter(c -> Character.isLowerCase(c)).count();
        final long digitCharacter = password.chars().filter(c -> Character.isDigit(c)).count();

        long poolSize = 0;
        if (lowerCaseLetters > 0)
            poolSize += LOWERCASE_POOL_SIZE;
        if (upperCase > 0)
            poolSize += UPPERCASE_POOL_SIZE;
        if (digitCharacter > 0)
            poolSize += DIGIT_POOL_SIZE;
        if (specialCharacters > 0)
            poolSize += SPECIAL_CHARACTERS_POOL_SIZE;

        long entropy = (long)(Math.log(Math.pow(poolSize, password.length())) / Math.log(2));
        return entropy;
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        long passwordEntropy = calculatePasswordEntropy(password);
        return getPasswordEntropyClass(passwordEntropy).ordinal() >= STRONG.ordinal();
    }
}
