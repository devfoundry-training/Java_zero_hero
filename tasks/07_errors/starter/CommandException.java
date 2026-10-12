/**
 * The user typed something we cannot understand (starter).
 * TODO Core 5 and Stretch 2: a second constructor that also takes the cause.
 */
public class CommandException extends LibraryException {

    public CommandException(String message) {
        super(message);
    }
}
