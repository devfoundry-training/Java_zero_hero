import java.io.IOException;
import java.nio.file.Path;

/**
 * Task 08 - Persistence (starter).
 *
 * The library classes and exceptions are the task 07 solution. Storage.java is
 * a stub: it loads and saves the files. Fill in each part marked TODO, then run
 * the check (see tasks/README.md). The program writes out/ in the current folder.
 *
 * Run from the repo root (DATA_DIR defaults to tasks/data):
 *   mvn -q compile exec:java@task -Dtask=08_persistence/starter -Dexec.args="tasks/data"
 */
public class Main {

    static final Path OUT = Path.of("out");

    public static void main(String[] args) throws IOException {
        Path data = Path.of(args.length > 0 ? args[0] : "tasks/data");
        // TODO Core 1: load books.csv and members.csv from the data folder
        // TODO Core 2: save out/books.csv and out/library.json
        // TODO Core 3: load out/books.csv back and compare it with the library
        // TODO Core 4: try missing.csv (catch NoSuchFileException)
        // TODO Core 5: load books_corrupt.csv and report the bad lines
        System.out.println("Core 1 to 5: not implemented");
    }
}
