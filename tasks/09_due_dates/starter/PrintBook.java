/** A paper book: its length is in pages. */
public class PrintBook extends Book {

    public PrintBook(int id, String title, String author, int year, String genre, int copies, int length) {
        super(id, title, author, year, genre, copies, length);
    }

    @Override
    public String formatName() {
        return "print";
    }

    @Override
    public String lengthText() {
        return getLength() + " pages";
    }
}
