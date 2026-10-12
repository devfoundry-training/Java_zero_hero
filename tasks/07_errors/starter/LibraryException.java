/**
 * The base class for every rule the library can refuse (starter).
 * It is unchecked (a RuntimeException), so callers are not forced to catch it on every line.
 */
public class LibraryException extends RuntimeException {

    public LibraryException(String message) {
        super(message);
    }
}
