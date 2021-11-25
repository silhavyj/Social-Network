package cz.zcu.kiv.pia.silhavyj.socialnetwork.exception;

/***
 * Custom SignUpException
 *
 * @author Jakub Silhavy (A21N0072P)
 */
public class SignUpException extends RuntimeException {

    /***
     * Creates an instance of SignUpException
     * @param message message passed into the exception
     */
    public SignUpException(String message) {
        super(message);
    }
}
