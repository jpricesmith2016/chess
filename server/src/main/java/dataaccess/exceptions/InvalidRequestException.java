package dataaccess.exceptions;

public class InvalidRequestException extends DataAccessException {
    public InvalidRequestException() {
        super("bad request");
    }
}
