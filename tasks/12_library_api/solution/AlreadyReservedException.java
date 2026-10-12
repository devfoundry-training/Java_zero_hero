/** A member cannot be in the same queue twice. */
public class AlreadyReservedException extends LibraryException {

    public AlreadyReservedException(String name, String title) {
        super(name + " has already reserved " + title);
    }
}
