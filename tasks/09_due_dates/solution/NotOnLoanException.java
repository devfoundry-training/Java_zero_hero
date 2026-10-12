/** The member is returning a book they do not have. */
public class NotOnLoanException extends LibraryException {

    public NotOnLoanException(String title, String name) {
        super(title + " is not on loan to " + name);
    }
}
