package cz.zcu.kiv.pia.silhavyj.socialnetwork.exceptions;

public class ResetPasswordEmailNotFoundException extends RuntimeException {

    public ResetPasswordEmailNotFoundException(String message) {
        super(message);
    }
}
