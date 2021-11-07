package cz.zcu.kiv.pia.silhavyj.socialnetwork.constant;

public class RegistrationConstants {

    public final static String EMAIL_NOT_FOUND_ERR_MSG = "E-mail address not found";
    public final static String REGISTRATION_TOKEN_ALREADY_ISSUED_ERR_MSG = "There is already a registration token issued for this account. You need to wait until the previous one expires (remaining seconds: %s)";
    public final static String RESET_PASSWORD_TOKEN_ALREADY_ISSUED_ERR_MSG = "There is already a reset password token issued for this account. You need to wait until the previous one expires (remaining seconds: %s)";
    public final static String INVALID_REGISTRATION_TOKEN = "Invalid registration token";
    public final static String RESET_PASSWORD_TOKEN_ALREADY_EXPIRED_ERR_MSG = "This token has already expired";

    public static final String SIGN_IN_FORM_INFO_MSG_NAME = "info_msg";
    public static final String SIGN_IN_FORM_SUCCESS_MSG_NAME = "success_msg";
    public static final String SIGN_IN_FORM_ERR_MSG_NAME = "error_msg";
    public static final String SIGN_UP_ERROR_MSG_NAME = "error_msg";
    public static final String RESET_PASSWORD_ERR_MSG_NAME = "error_msg";

    public static final String INVALID_CREDENTIALS_ERR_MSG = "Invalid credentials";
    public static final String LOCKED_ACCOUNT_ERR_MSG = "Your account has been deleted, please contact the administrator";
    public static final String RESET_PASSWORD_EMAIL_NOT_FOUND_ERR_MSG = "E-mail address not found";
    public static final String EMAIL_ALREADY_TAKEN_ERR_MSG = "This e-mail address is already taken";
    public static final String PASSWORDS_ARE_NOT_THE_SAME_ERR_MSG = "Passwords are required to be the same";

    public static final String LOCKED_ACCOUNT_FLAG = "locked";

    public static final String ACCOUNT_HAS_BEEN_ACTIVATED_INFO_MSG = "Your account has been successfully activated";

    public static final String CONFIRM_CHANGE_PASSWORD_INFO_MSG = "Please confirm your intention to change the password through e-mail";
    public static final String CONFIRM_EMAIL_ADDRESS_INFO_MSG = "Please confirm your e-mail address to finish the registration";
}
