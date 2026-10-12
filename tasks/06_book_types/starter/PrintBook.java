/**
 * A paper book (starter): format "print", length in pages.
 *
 * TODO Core 1 and 2: make Book abstract with a length field and the abstract
 * methods formatName() and lengthText(), then let this class override them.
 * Book.java is still the task 05 class, so for now the length is kept here.
 */
public class PrintBook extends Book {

    private final int length;

    public PrintBook(int id, String title, String author, int year, String genre, int copies, int length) {
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
