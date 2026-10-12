/**
 * A book in the library (starter).
 *
 * TODO Core 1 and 3: private final fields, a constructor that refuses bad values
 * with IllegalArgumentException (see the messages in README.md), getters, a
 * private count of copies on loan, available(), lend(), takeBack() and toString().
 */
public class Book {

    private final int id;
    private final String title;

    public Book(int id, String title, String author, int year, String genre, int copies) {
        // TODO validate every argument, then keep the rest of the fields
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /** Copies minus copies on loan. */
    public int available() {
        throw new UnsupportedOperationException("not implemented");
    }

    /** Takes one copy off the shelf. Returns false when there is none to take. */
    public boolean lend() {
        throw new UnsupportedOperationException("not implemented");
    }

    /** Puts one copy back. Returns false when no copy was out. */
    public boolean takeBack() {
        throw new UnsupportedOperationException("not implemented");
    }

    /** "#ID TITLE by AUTHOR (YEAR), AVAILABLE of COPIES available" */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("not implemented");
    }
}
