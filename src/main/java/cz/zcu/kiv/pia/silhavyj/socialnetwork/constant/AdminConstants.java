package cz.zcu.kiv.pia.silhavyj.socialnetwork.constant;

/***
 * Constants related to Admins. These messages are used when escalating user privileges to Admin.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public class AdminConstants {

    public static final String CANNOT_CHANGE_PRIVILEGES_ERR_MSG = "You cannot change role of someone who you are not friends with";
    public static final String ADMIN_PRIVILEGES_ALREADY_ASSIGNED_ERR_MSG = "Your friend already has admin privileges";
    public static final String MISSING_ADMIN_PRIVILEGES_ERR_MSG = "You've been unsigned admin privileges by another admin. Please, log out and log back in";
    public static final String USER_HAS_NO_ADMIN_PRIVILEGES_ERR_MSG = "User does not have any user admin privileges";
    public static final String ADMIN_PRIVILEGES_SUCCESSFULLY_ASSIGNED_INFO_MSG = "User has been successfully assigned admin privileges";
    public static final String ADMIN_PRIVILEGES_SUCCESSFULLY_UNSIGNED_INFO_MSG = "User has been successfully unsigned admin privileges";
}
