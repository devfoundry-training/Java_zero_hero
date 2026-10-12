/** An audio book: its length is in minutes, shown as hours and minutes. */
public class AudioBook extends Book {

    public AudioBook(int id, String title, String author, int year, String genre, int copies, int length) {
        super(id, title, author, year, genre, copies, length);
    }

    @Override
    public String formatName() {
        return "audio";
    }

    /** 1150 minutes is "19h 10m": integer division and remainder by 60. */
    @Override
    public String lengthText() {
        return String.format("%dh %02dm", getLength() / 60, getLength() % 60);
    }
}
