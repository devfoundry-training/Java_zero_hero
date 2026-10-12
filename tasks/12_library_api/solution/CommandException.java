/**
 * The user typed something we cannot understand.
 * The second constructor keeps the original exception as the cause
 * (exception chaining), so nothing about the first failure is lost.
 */
public class CommandException extends LibraryException {

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
