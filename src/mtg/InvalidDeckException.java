package mtg;

/**
 * @author Jaroslaw Pawlak
 */
public class InvalidDeckException extends RuntimeException {
    public InvalidDeckException(String message) {
        super(message);
    }
}
