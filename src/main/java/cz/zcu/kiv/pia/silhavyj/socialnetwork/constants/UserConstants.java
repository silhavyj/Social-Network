package cz.zcu.kiv.pia.silhavyj.socialnetwork.constants;

public class UserConstants {

    public static final int FIRSTNAME_MIN_LENGTH = 2;
    public static final int FIRSTNAME_MAX_LENGTH = 64;
    public static final String FIRSTNAME_EMPTY_ERR_MSG = "Firstname cannot be empty";
    public static final String FIRSTNAME_INVALID_LENGTH_ERR_MSG = "Firstname length should be between " + FIRSTNAME_MIN_LENGTH + " and " + FIRSTNAME_MAX_LENGTH;

    public static final int LASTNAME_MIN_LENGTH = 2;
    public static final int LASTNAME_MAX_LENGTH = 64;
    public static final String LASTNAME_EMPTY_ERR_MSG = "Lastname cannot be empty";
    public static final String LASTNAME_INVALID_LENGTH_ERR_MSG = "Lastname length should be between " + FIRSTNAME_MIN_LENGTH + " and " + FIRSTNAME_MAX_LENGTH;

    public static final String INSECURE_PASSWORD_ERR_MSG = "Password is not secure enough";

    public static final String DOB_EMPTY_ERR_MSG = "Date of birth cannot be empty";

    public static final String EMAIL_EMPTY_ERR_MSG = "E-mail cannot be empty";
    public static final String EMAIL_INVALID_ERR_MSG = "Invalid e-mail address";
}

