import java.time.LocalDate;
import java.util.Optional;

/**
 * One loan: who borrowed which book, on which day, and when it came back.
 *
 * LocalDate is a calendar day with no time of day and no time zone, which is
 * exactly what a library loan needs. plusDays moves across month ends, year
 * ends and leap days correctly.
 */
public class Loan {

    private final int id;
    private final int bookId;
    private final int memberId;
    private final LocalDate borrowed;
    private LocalDate returned;     // null while the book is still out

    public Loan(int id, int bookId, int memberId, LocalDate borrowed, LocalDate returned) {
        if (returned != null && returned.isBefore(borrowed)) {
            throw new IllegalArgumentException("returned must not be before borrowed");
        }
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowed = borrowed;
        this.returned = returned;
    }

    /** A loan that is still out. */
    public Loan(int id, int bookId, int memberId, LocalDate borrowed) {
        this(id, bookId, memberId, borrowed, null);
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

    /** The nullable field is kept inside; callers get an Optional and must say what "no date" means. */
    public Optional<LocalDate> getReturned() {
        return Optional.ofNullable(returned);
    }

    public boolean isOpen() {
        return returned == null;
    }

    void markReturned(LocalDate day) {
        this.returned = day;
    }
}
