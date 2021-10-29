package cz.zcu.kiv.pia.silhavyj.socialnetwork.service.passwordgen;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Service;

import static cz.zcu.kiv.pia.silhavyj.socialnetwork.model.user.validation.password.PasswordConstants.*;

@Service
public class RandomPasswordGenerator implements IPasswordGenerator {

    @Override
    public String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);

        lowerCaseRule.setNumberOfCharacters(MINIMAL_NUMBER_OF_LOWERCASE_CHARACTERS_REQUIRED);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(MINIMAL_NUMBER_OF_UPPERCASE_CHARACTERS_REQUIRED);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(MINIMAL_NUMBER_OF_DIGIT_CHARACTERS_REQUIRED);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "Error attaining special character";
            }
            public String getCharacters() {
                return "!@#$^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(MINIMAL_NUMBER_OF_SPECIAL_CHARACTERS_REQUIRED);

        String password = gen.generatePassword(MINIMAL_PASSWORD_LENGTH, splCharRule, lowerCaseRule, upperCaseRule, digitRule);
        return password;
    }
}
