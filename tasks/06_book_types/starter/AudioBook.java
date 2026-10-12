/**
 * An audio book (starter): format "audio", length in minutes.
 * TODO Core 2: lengthText() shows the minutes as "Hh MMm" (1150 is "19h 10m", 480 is "8h 00m").
 */
public class AudioBook extends Book {

    private final int length;

    public AudioBook(int id, String title, String author, int year, String genre, int copies, int length) {
        super(id, title, author, year, genre, copies);
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public String formatName() {
        throw new UnsupportedOperationException("not implemented");
    }

    public String lengthText() {
        throw new UnsupportedOperationException("not implemented");
    }
}
