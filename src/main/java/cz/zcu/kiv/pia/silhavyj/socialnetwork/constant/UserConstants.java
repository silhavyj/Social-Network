package cz.zcu.kiv.pia.silhavyj.socialnetwork.constant;

/***
 * Constants related to User. These messages and constants are used when validating attributes in the User class.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public class UserConstants {

    public static final int FIRSTNAME_MIN_LENGTH = 2;
    public static final int FIRSTNAME_MAX_LENGTH = 64;
    public static final int LASTNAME_MIN_LENGTH = 2;
    public static final int LASTNAME_MAX_LENGTH = 64;
    public static final String FIRSTNAME_EMPTY_ERR_MSG = "Firstname cannot be empty";
    public static final String FIRSTNAME_INVALID_LENGTH_ERR_MSG = "Firstname length should be between " + FIRSTNAME_MIN_LENGTH + " and " + FIRSTNAME_MAX_LENGTH;
    public static final String LASTNAME_EMPTY_ERR_MSG = "Lastname cannot be empty";
    public static final String LASTNAME_INVALID_LENGTH_ERR_MSG = "Lastname length should be between " + FIRSTNAME_MIN_LENGTH + " and " + FIRSTNAME_MAX_LENGTH;
    public static final String INSECURE_PASSWORD_ERR_MSG = "Password is not secure enough";
    public static final String DOB_EMPTY_ERR_MSG = "Date of birth cannot be empty";
    public static final String EMAIL_EMPTY_ERR_MSG = "E-mail cannot be empty";
    public static final String EMAIL_INVALID_ERR_MSG = "Invalid e-mail address";
    public static final String PROFILE_IMAGES_DIRECTORY = "images";
    public static final String DEFAULT_PROFILE_IMAGE_PATH =  PROFILE_IMAGES_DIRECTORY + "/" + "default_user_picture.png";
    public static final String CREATING_PROFILE_PIC_DIR_FAILED_ERR_MSG = "Creating a directory to store profile pictures failed";
    public static final String SAVING_PROFILE_PICTURE_FAILED_ERR_MSG = "Storing an image into the profile picture directory failed";
}