/** An electronic book: its length is in pages, like a print book. */
public class EBook extends Book {

    public EBook(int id, String title, String author, int year, String genre, int copies, int length) {
        super(id, title, author, year, genre, copies, length);
    }

    @Override
    public String formatName() {
        return "ebook";
    }

    @Override
    public String lengthText() {
        return getLength() + " pages";
    }
}
