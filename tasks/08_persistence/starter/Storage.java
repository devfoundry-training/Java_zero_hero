import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Task 08 - storage (starter): CSV and JSON in and out.
 *
 * Read and write every file as UTF-8 (Files.readAllLines(path, StandardCharsets.UTF_8),
 * Files.writeString(path, text, StandardCharsets.UTF_8)). A missing file throws
 * NoSuchFileException; let it reach the caller.
 */
public final class Storage {

    static final List<String> BOOK_FIELDS = List.of("id", "title", "author", "year", "genre", "copies", "format", "length");
    static final List<String> MEMBER_FIELDS = List.of("id", "name", "email", "type", "joined");

    private Storage() {
        // static members only: no objects of this class
    }

    /**
     * TODO Core 1 and 5: one book per good line. Report every bad line with
     * result.skip(lineNumber, reason) in the order of checks in README.md.
     */
    public static LoadResult loadBooksCsv(Path path) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 1: one member per good line. */
    public static LoadResult loadMembersCsv(Path path) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 2: the same header and columns as data/books.csv, "\n" line endings. */
    public static void saveBooksCsv(Library library, Path path) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 2: the shape in README.md, written by hand (escape " and \ in text). */
    public static void saveJson(Library library, Path path) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    /** Puts the loaded books and members into a new Library. */
    public static Library buildLibrary(LoadResult result) {
        Library library = new Library();
        for (Book book : result.books) {
            library.addBook(book);
        }
        for (Member member : result.members) {
            library.addMember(member);
        }
        return library;
    }
}
