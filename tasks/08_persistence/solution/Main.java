import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 08 - Persistence (solution, Core + Stretch).
 *
 * Session 8: file I/O. Builds on task 07: the data now comes from the files in
 * the data folder, and the library is saved to CSV and JSON in a folder called
 * out (made in the current folder). Core reads the CSV back; reading the JSON
 * back is the Stretch.
 *
 * Run from the repo root (DATA_DIR defaults to tasks/data):
 *   mvn -q compile exec:java@task -Dtask=08_persistence/solution -Dexec.args="tasks/data"
 *   mvn -q compile exec:java@task -Dtask=08_persistence/solution -Dexec.args="tasks/data --stretch"
 */
public class Main {

    static final Path OUT = Path.of("out");

    /** Anything that loads a file into a LoadResult: loadBooksCsv, loadJson, ... */
    interface Loader {
        LoadResult load(Path path) throws IOException;
    }

    public static void main(String[] args) throws IOException {
        List<String> positional = new ArrayList<>();
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                positional.add(arg);
            }
        }
        Path data = Path.of(positional.isEmpty() ? "tasks/data" : positional.get(0));
        boolean stretch = List.of(args).contains("--stretch");

        // ---- Core 1: load the real data from disk ----
        LoadResult loaded = Storage.loadBooksCsv(data.resolve("books.csv"));
        LoadResult members = Storage.loadMembersCsv(data.resolve("members.csv"));
        System.out.println("Loaded " + loaded.books.size() + " books from books.csv");
        System.out.println("Loaded " + members.members.size() + " members from members.csv");
        loaded.members.addAll(members.members);
        Library library = Storage.buildLibrary(loaded);

        // ---- Core 2 and 3: save as CSV and JSON, then load the CSV back and compare ----
        Storage.saveBooksCsv(library, OUT.resolve("books.csv"));
        System.out.println("Saved " + library.bookCount() + " books to out/books.csv");
        Storage.saveJson(library, OUT.resolve("library.json"));
        System.out.println("Saved " + library.bookCount() + " books and " + library.memberCount()
                + " members to out/library.json");

        Library fromCsv = Storage.buildLibrary(Storage.loadBooksCsv(OUT.resolve("books.csv")));
        String verdict = sameBooks(library, fromCsv) ? "same as before" : "DIFFERENT";
        System.out.println("Reloaded out/books.csv: " + fromCsv.bookCount() + " books, " + verdict);

        // ---- Core 4 and 5: a missing file and a corrupt CSV ----
        tryLoad("missing.csv", Storage::loadBooksCsv, data.resolve("missing.csv"));
        tryLoad("books_corrupt.csv", Storage::loadBooksCsv, data.resolve("books_corrupt.csv"));

        if (stretch) {
            printStretch(library, data);
        }
    }

    /** Loads a file that might be missing or broken, and says what happened. */
    static void tryLoad(String name, Loader loader, Path path) throws IOException {
        System.out.println("Loading " + name);
        LoadResult result;
        try {
            result = loader.load(path);
        } catch (NoSuchFileException error) {
            System.out.println("  file not found, starting with an empty catalogue");
            return;
        } catch (IllegalArgumentException error) {
            System.out.println("  " + error.getMessage() + ", starting with an empty catalogue");
            return;
        }
        for (String problem : result.problems) {
            System.out.println("  " + problem);
        }
        System.out.println("  loaded " + result.books.size() + " books, skipped " + result.skipped + " lines");
    }

    /** Same ids with the same field values, in the same order. */
    static boolean sameBooks(Library a, Library b) {
        return bookRows(a).equals(bookRows(b));
    }

    static List<String> bookRows(Library library) {
        List<String> rows = new ArrayList<>();
        for (Book book : library.allBooks()) {
            rows.add(Storage.bookRow(book));
        }
        return rows;
    }

    static boolean sameMembers(Library a, Library b) {
        return memberRows(a).equals(memberRows(b));
    }

    static List<String> memberRows(Library library) {
        List<String> rows = new ArrayList<>();
        for (Member member : library.allMembers()) {
            rows.add(Storage.memberJson(member));
        }
        return rows;
    }

    // ---- Stretch: read the JSON back, cope with broken JSON, keep loans across a restart ----

    static void printStretch(Library library, Path data) throws IOException {
        System.out.println("Stretch");
        Library fromJson = Storage.buildLibrary(Storage.loadJson(OUT.resolve("library.json")));
        boolean same = sameBooks(library, fromJson) && sameMembers(library, fromJson);
        System.out.println("Reloaded out/library.json: " + fromJson.bookCount() + " books and "
                + fromJson.memberCount() + " members, " + (same ? "same as before" : "DIFFERENT"));
        tryLoad("library_corrupt.json", Storage::loadJson, data.resolve("library_corrupt.json"));

        library.borrow(101, 4);
        library.borrow(101, 7);
        Storage.saveJson(library, OUT.resolve("library.json"));
        Library restarted = Storage.buildLibrary(Storage.loadJson(OUT.resolve("library.json")));
        System.out.println("after restart: " + restarted.member(101));
        System.out.println("after restart: " + restarted.book(4));
    }
}
