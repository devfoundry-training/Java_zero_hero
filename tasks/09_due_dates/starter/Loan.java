import java.time.LocalDate;
import java.util.Optional;

/**
 * One loan (starter): who borrowed which book, on which day, and when it came back.
 *
 * TODO Core 2: keep returned as a nullable field (null while the book is out)
 * and hand it out as an Optional, so callers must say what "no date" means.
 */
public class Loan {

    private final int id;
    private final int bookId;
    private final int memberId;
    private final LocalDate borrowed;

    public Loan(int id, int bookId, int memberId, LocalDate borrowed, LocalDate returned) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowed = borrowed;
        // TODO keep returned
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDate getBorrowed() {
        return borrowed;
    }

    public Optional<LocalDate> getReturned() {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean isOpen() {
        throw new UnsupportedOperationException("not implemented");
    }
}
