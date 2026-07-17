package dataaccess.exceptions;

public class InvalidSessionException extends DataAccessException {
    public InvalidSessionException() {
        super("unauthorized");
    }
}
