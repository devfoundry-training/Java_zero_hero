/** A member tried to reserve a book that has a free copy: they should borrow it instead. */
public class ReservationNotNeededException extends LibraryException {

    public ReservationNotNeededException(String title) {
        super(title + " has copies available, borrow it instead");
    }
}
