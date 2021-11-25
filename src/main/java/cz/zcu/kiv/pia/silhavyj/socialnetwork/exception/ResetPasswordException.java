package cz.zcu.kiv.pia.silhavyj.socialnetwork.exception;

/***
 * Custom ResetPasswordException
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public class ResetPasswordException extends RuntimeException {

    /***
     * Creates an instance of ResetPasswordException
     * @param message message passed into the exception
     */
    public ResetPasswordException(String message) {
        super(message);
    }
}
