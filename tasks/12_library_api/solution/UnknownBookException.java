/** There is no book with that id. The exception builds its own message. */
public class UnknownBookException extends LibraryException {

    public UnknownBookException(int bookId) {
        super("no book with id " + bookId);
    }
}
