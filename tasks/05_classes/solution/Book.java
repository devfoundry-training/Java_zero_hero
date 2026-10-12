import java.util.Comparator;
import java.util.Locale;

/**
 * A book in the library: validated when it is created, and in charge of its own
 * count of copies on loan.
 *
 * Stretch 1: two books are equal when their ids are equal (equals and hashCode).
 * Stretch 2: the natural order is title A-Z ignoring case, then year (Comparable).
 */
public class Book implements Comparable<Book> {

    private static final Comparator<Book> NATURAL_ORDER = Comparator
            .comparing((Book book) -> book.title.toLowerCase(Locale.ROOT))
            .thenComparingInt(book -> book.year);

    private final int id;
    private final String title;
    private final String author;
    private final int year;
    private final String genre;
    private final int copies;
    private int onLoan;         // private: only lend() and takeBack() may change it

    public Book(int id, String title, String author, int year, String genre, int copies) {
        // Validation in the constructor: a Book that exists is a valid Book
        if (id <= 0) {
            throw new IllegalArgumentException("book id must be positive");
        }
        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (year < 1450 || year > 2100) {
            throw new IllegalArgumentException("year must be between 1450 and 2100");
        }
        if (copies < 0) {
            throw new IllegalArgumentException("copies must not be negative");
        }
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.copies = copies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public int getCopies() {
        return copies;
    }

    public int available() {
        return copies - onLoan;
    }

    /** Takes one copy off the shelf. Returns false when there is none to take. */
    public boolean lend() {
        if (available() == 0) {
            return false;
        }
        onLoan++;
        return true;
    }

    /** Puts one copy back. Returns false when no copy was out. */
    public boolean takeBack() {
        if (onLoan == 0) {
            return false;
        }
        onLoan--;
        return true;
    }

    @Override
    public String toString() {
        return "#" + id + " " + title + " by " + author + " (" + year + "), "
                + available() + " of " + copies + " available";
    }

    // ---- Stretch 1: identity is the id ----

    @Override
    public boolean equals(Object other) {
        return other instanceof Book book && book.id == id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);    // equal books must give equal hash codes
    }

    // ---- Stretch 2: natural order ----

    @Override
    public int compareTo(Book other) {
        return NATURAL_ORDER.compare(this, other);
    }
}
