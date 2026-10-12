/**
 * The abstract base of every book type. It holds the fields and rules that all
 * books share; each subclass only says what makes it different (its format name
 * and how its length reads).
 *
 * abstract: there is no plain "Book" on the shelf, only a PrintBook, an EBook
 * or an AudioBook, so new Book(...) is a compile error.
 */
public abstract class Book {

    private final int id;
    private final String title;
    private final String author;
    private final int year;
    private final String genre;
    private final int copies;
    private final int length;   // pages for print and ebook, minutes for audio
    private int onLoan;

    protected Book(int id, String title, String author, int year, String genre, int copies, int length) {
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
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.copies = copies;
        this.length = length;
    }

    /**
     * Factory: the format column decides which class to build.
     * fields are the eight CSV columns: id,title,author,year,genre,copies,format,length.
     */
    public static Book makeBook(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        int year = Integer.parseInt(fields[3]);
        int copies = Integer.parseInt(fields[5]);
        int length = Integer.parseInt(fields[7]);
        return switch (fields[6]) {
            case "print" -> new PrintBook(id, fields[1], fields[2], year, fields[4], copies, length);
            case "ebook" -> new EBook(id, fields[1], fields[2], year, fields[4], copies, length);
            case "audio" -> new AudioBook(id, fields[1], fields[2], year, fields[4], copies, length);
            default -> throw new IllegalArgumentException(
                    "format must be print, ebook or audio, got '" + fields[6] + "'");
        };
    }

    // ---- What each subclass must provide ----

    public abstract String formatName();

    public abstract String lengthText();

    /** Written once, here. Polymorphism: the same call works for every book type. */
    public String describe() {
        return "#" + id + " " + title + ": " + formatName() + ", " + lengthText();
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

    public int getLength() {
        return length;
    }

    public int available() {
        return copies - onLoan;
    }

    public boolean lend() {
        if (available() == 0) {
            return false;
        }
        onLoan++;
        return true;
    }

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

    @Override
    public boolean equals(Object other) {
        return other instanceof Book book && book.id == id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
