import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Task 10 - the library in SQLite, through JDBC (starter).
 *
 * The driver is org.xerial:sqlite-jdbc, already in pom.xml. The constructor
 * opens the database file and switches on foreign keys; the rest is yours.
 *
 * Parameterised queries only: PreparedStatement with ? and setInt / setString.
 * Never build SQL by joining strings with values. Close every Statement and
 * ResultSet with try-with-resources.
 */
public class LibraryDb implements AutoCloseable {

    private final Connection conn;

    public LibraryDb(Path file) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + file);
        try (Statement statement = conn.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }

    /**
     * TODO Core 2: run data/schema.sql. JDBC runs one statement at a time, so
     * split the text on ";" and skip the pieces that are only blank space.
     */
    public void create(Path schema) throws IOException, SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 3: insert every row with addBatch / executeBatch, inside one transaction. */
    public void importRows(LoadResult rows) throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 3: SELECT COUNT(*) from one of books, members or loans. */
    public int count(String table) throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 5: build the task 09 model from the tables (not from the CSV files). */
    public Library loadLibrary() throws SQLException {
        throw new UnsupportedOperationException("not implemented");
    }
}
