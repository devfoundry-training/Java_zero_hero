/**
 * The base class for every rule the library can refuse.
 *
 * It extends RuntimeException, so it is unchecked: callers are not forced to
 * catch it on every line, and the command loop catches it in one place.
 * Catch LibraryException to handle every library problem; catch a subclass
 * when one problem needs special handling.
 */
public class LibraryException extends RuntimeException {

    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
