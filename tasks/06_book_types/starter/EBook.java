/**
 * An electronic book (starter): format "ebook", length in pages.
 * TODO Core 2: the same shape as PrintBook.
 */
public class EBook extends Book {

    private final int length;

    public EBook(int id, String title, String author, int year, String genre, int copies, int length) {
        super(id, title, author, year, genre, copies);
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public String formatName() {
        throw new UnsupportedOperationException("not implemented");
    }

    /** "N pages" */
    public String lengthText() {
        throw new UnsupportedOperationException("not implemented");
    }
}
