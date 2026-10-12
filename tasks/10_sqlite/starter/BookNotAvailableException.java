/** Every copy of the book is out. */
public class BookNotAvailableException extends LibraryException {

    public BookNotAvailableException(String title) {
        super(title + " has no copies available");
    }
}
