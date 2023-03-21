package pt.jorgeduarte.app.exceptions;

public class FileNotFoundException extends RuntimeException{
    public FileNotFoundException(String message) {
        super(message);
    }
}
