package pl.kurs.exception;

public class InvalidDataAccessApiUsageException extends RuntimeException {
    public InvalidDataAccessApiUsageException(String message) {
        super(message);
    }
}
