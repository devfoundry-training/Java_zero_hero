package com.teaching.databases;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class that centralises H2 connection details and schema initialisation.
 *
 * H2 in-memory URL breakdown:
 *   jdbc:h2:mem:teaching - in-memory database named "teaching"
 *                            All connections to the same name share the same data
 *                            within a single JVM (important for multi-connection demos)
 *   DB_CLOSE_DELAY=-1 - keep the database alive for the full JVM lifetime;
 *                            without this, H2 drops the in-memory DB the moment
 *                            the last connection closes
 *
 * To switch to MySQL: replace URL, USER, PASSWORD and ensure mysql-connector-j
 * is on the classpath (it already is - see pom.xml). DAO code is unchanged.
 *
 * Production note: in a real application you would inject a javax.sql.DataSource
 * (e.g. HikariCP connection pool) rather than calling DriverManager directly.
 * DriverManager is fine for learning - it creates a new connection on every call.
 */
public final class DbConnection {

    private static final String URL      = "jdbc:h2:mem:teaching;DB_CLOSE_DELAY=-1";
    private static final String USER     = "sa";
    private static final String PASSWORD = "";

    // Utility class - prevent instantiation
    private DbConnection() {}

    /**
     * Returns a new JDBC connection. The caller is responsible for closing it -
     * always use try-with-resources: try (Connection conn = DbConnection.getConnection())
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Loads src/main/resources/databases/schema.sql from the classpath and
     * executes it against the in-memory database.
     *
     * Must be called once before any DAO operations. Safe to call again -
     * the DDL uses CREATE TABLE IF NOT EXISTS.
     *
     * @throws IOException  if schema.sql cannot be read from the classpath
     * @throws SQLException if the DDL statement fails
     */
    public static void initSchema() throws IOException, SQLException {
        // getResourceAsStream with a leading '/' is relative to the classpath root.
        // Maven puts src/main/resources/ at the classpath root, so
        // /databases/schema.sql maps to src/main/resources/databases/schema.sql.
        try (InputStream is = DbConnection.class.getResourceAsStream("/databases/schema.sql")) {

            if (is == null) {
                throw new IllegalStateException(
                    "schema.sql not found at /databases/schema.sql on the classpath. " +
                    "Ensure src/main/resources/databases/schema.sql exists and Maven has compiled resources."
                );
            }

            String ddl = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            try (Connection conn = getConnection();
                 Statement  stmt = conn.createStatement()) {
                stmt.execute(ddl);
            }
        }
    }
}
