package pt.jorgeduarte.app.exceptions;

public class InvalidFilenameException extends RuntimeException{
    public InvalidFilenameException(String message) {
        super(message);
    }
}
