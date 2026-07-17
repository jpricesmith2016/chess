package dataaccess.exceptions;

public class InvalidUserException extends DataAccessException {
    public InvalidUserException() {
        super("unauthorized");
    }
}
