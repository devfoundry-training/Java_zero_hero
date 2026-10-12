import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Task 10 - SQLite storage (starter).
 *
 * The library classes and Storage are the task 09 solution. LibraryDb.java is a
 * stub that already opens the database. Fill in each part marked TODO, then run
 * the check (see tasks/README.md). The database is out/library.db in the
 * current folder.
 *
 * Run from the repo root (DATA_DIR defaults to tasks/data, TODAY to 2026-10-12):
 *   mvn -q compile exec:java@task -Dtask=10_sqlite/starter -Dexec.args="tasks/data"
 */
public class Main {

    static final LocalDate DEFAULT_TODAY = LocalDate.of(2026, 10, 12);
    static final Path DB_PATH = Path.of("out", "library.db");

    public static void main(String[] args) throws IOException, SQLException {
        Path data = Path.of(args.length > 0 ? args[0] : "tasks/data");
        LocalDate today = args.length > 1 ? LocalDate.parse(args[1]) : DEFAULT_TODAY;
        Files.createDirectories(DB_PATH.getParent());
        Files.deleteIfExists(DB_PATH);      // start clean, so every run is the same
        try (LibraryDb db = new LibraryDb(DB_PATH)) {
            // TODO Core 2: db.create(data.resolve("schema.sql")), then "Created out/library.db from schema.sql"
            // TODO Core 3: import the three CSV files, then print the row counts from the database
            // TODO Core 4: the four SQL questions, in order
            // TODO Core 5 and 6: the three task 09 actions, saved with INSERT / UPDATE, then reload and report
            System.out.println("Core 2 to 6: not implemented");
        }
    }
}
