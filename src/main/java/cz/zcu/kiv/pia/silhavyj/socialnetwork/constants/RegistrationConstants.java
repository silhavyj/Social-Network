package cz.zcu.kiv.pia.silhavyj.socialnetwork.constants;

public class RegistrationConstants {

    public final static String EMAIL_NOT_FOUND_ERR_MSG = "E-mail address not found";
    public final static String REGISTRATION_TOKEN_ALREADY_ISSUED_ERR_MSG = "There is already a registration token issued for this account. You need to wait until the previous one expires (remaining seconds: %s)";

    public static final int REGISTRATION_TOKEN_MIN_VALIDATION = 1;
    public static final int RESET_PASSWORD_TOKEN_MIN_VALIDATION = 1;

    public static final String SIGN_IN_FORM_MSG_NAME = "msg";
    public static final String SIGN_UP_ERROR_MSG_NAME = "error_msg";
    public static final String RESET_PASSWORD_ERR_MSG_NAME = "error_msg";

    public static final String INVALID_CREDENTIALS_ERR_MSG = "Invalid credentials";
    public static final String LOCKED_ACCOUNT_ERR_MSG = "Your account has been deleted, please contact the administrator";
    public static final String RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG = "E-mail address not found";
    public static final String EMAIL_ALREADY_TAKEN_ERR_MSG = "This e-mail address is already taken";

    public static final String LOCKED_ACCOUNT_FLAG = "locked";
}
