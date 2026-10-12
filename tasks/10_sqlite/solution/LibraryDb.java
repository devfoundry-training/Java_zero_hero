import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Task 10 - the library in SQLite, through JDBC.
 *
 * Every query uses ? placeholders and passes the values separately with
 * setInt / setString. The driver keeps the values apart from the SQL text, so
 * a title like  x' OR '1'='1  is only ever data. Never build SQL by joining
 * strings with values.
 *
 * The driver is org.xerial:sqlite-jdbc (see pom.xml). Every Statement,
 * PreparedStatement and ResultSet is closed by try-with-resources.
 */
public class LibraryDb implements AutoCloseable {

    /** One row of each report query. */
    record GenreCount(String genre, int count) {
    }

    record ShelfRow(int id, String title, int available, int copies) {
    }

    record TitleRow(int id, String title, int year) {
    }

    record MemberCount(int id, String name, int openLoans) {
    }

    private final Connection conn;

    public LibraryDb(Path file) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + file);
        try (Statement statement = conn.createStatement()) {
            // SQLite ignores FOREIGN KEY rules unless this is switched on, once per connection
            statement.execute("PRAGMA foreign_keys = ON");
        }
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }

    // ---- Setting up ----

    /**
     * Runs schema.sql (it drops and recreates every table). JDBC runs one
     * statement at a time, so the file is split on ";". The schema promises
     * no semicolons inside comments or strings, which makes that safe.
     */
    public void create(Path schema) throws IOException, SQLException {
        String text = Files.readString(schema, StandardCharsets.UTF_8);
        try (Statement statement = conn.createStatement()) {
            for (String sql : text.split(";")) {
                if (!stripComments(sql).isBlank()) {
                    statement.execute(sql);
                }
            }
        }
    }

    /** The text with every "--" comment line removed: what is left is SQL, or nothing. */
    private static String stripComments(String sql) {
        StringBuilder out = new StringBuilder();
        for (String line : sql.split("\n")) {
            if (!line.strip().startsWith("--")) {
                out.append(line).append('\n');
            }
        }
        return out.toString();
    }

    /** Copies the loaded CSV rows into the tables, in one transaction. */
    public void importRows(LoadResult rows) throws SQLException {
        conn.setAutoCommit(false);      // from here, nothing is saved until commit()
        try (PreparedStatement books = conn.prepareStatement(
                "INSERT INTO books (id, title, author, year, genre, copies, format, length) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
             PreparedStatement members = conn.prepareStatement(
                     "INSERT INTO members (id, name, email, type, joined) VALUES (?, ?, ?, ?, ?)");
             PreparedStatement loans = conn.prepareStatement(
                     "INSERT INTO loans (id, book_id, member_id, borrowed, returned) VALUES (?, ?, ?, ?, ?)")) {
            for (Book book : rows.books) {
                books.setInt(1, book.getId());
                books.setString(2, book.getTitle());
                books.setString(3, book.getAuthor());
                books.setInt(4, book.getYear());
                books.setString(5, book.getGenre());
                books.setInt(6, book.getCopies());
                books.setString(7, book.formatName());
                books.setInt(8, book.getLength());
                books.addBatch();       // queue the row; executeBatch sends them all
            }
            books.executeBatch();
            for (Member member : rows.members) {
                members.setInt(1, member.getId());
                members.setString(2, member.getName());
                members.setString(3, member.getEmail());
                members.setString(4, member.getType());
                members.setString(5, member.getJoined());
                members.addBatch();
            }
            members.executeBatch();
            for (Loan loan : rows.loans) {
                loans.setInt(1, loan.getId());
                loans.setInt(2, loan.getBookId());
                loans.setInt(3, loan.getMemberId());
                loans.setString(4, loan.getBorrowed().toString());
                loans.setString(5, loan.getReturned().map(LocalDate::toString).orElse(null));   // null for SQL NULL
                loans.addBatch();
            }
            loans.executeBatch();
            conn.commit();
        } catch (SQLException error) {
            conn.rollback();            // all or nothing: no half-imported tables
            throw error;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public int count(String table) throws SQLException {
        // A table name cannot be a ? parameter, so it is checked against a fixed list instead of trusted
        if (!Set.of("books", "members", "loans").contains(table)) {
            throw new IllegalArgumentException("unknown table " + table);
        }
        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    // ---- Queries ----

    public List<GenreCount> genreCounts() throws SQLException {
        List<GenreCount> rows = new ArrayList<>();
        try (PreparedStatement query = conn.prepareStatement(
                "SELECT genre, COUNT(*) FROM books GROUP BY genre ORDER BY genre");
             ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                rows.add(new GenreCount(rs.getString(1), rs.getInt(2)));
            }
        }
        return rows;
    }

    /** Books with nothing on the shelf: copies minus open loans is 0. */
    public List<ShelfRow> unavailableBooks() throws SQLException {
        String sql = """
                SELECT b.id, b.title, b.copies - COUNT(l.id) AS available, b.copies
                FROM books b
                LEFT JOIN loans l ON l.book_id = b.id AND l.returned IS NULL
                GROUP BY b.id, b.title, b.copies
                HAVING b.copies - COUNT(l.id) = 0
                ORDER BY b.id""";
        List<ShelfRow> rows = new ArrayList<>();
        try (PreparedStatement query = conn.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                rows.add(new ShelfRow(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4)));
            }
        }
        return rows;
    }

    public List<TitleRow> searchTitles(String text) throws SQLException {
        List<TitleRow> rows = new ArrayList<>();
        try (PreparedStatement query = conn.prepareStatement(
                "SELECT id, title, year FROM books WHERE LOWER(title) LIKE ? ORDER BY id")) {
            // The % wildcards go into the value, not into the SQL
            query.setString(1, "%" + text.toLowerCase(Locale.ROOT) + "%");
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    rows.add(new TitleRow(rs.getInt(1), rs.getString(2), rs.getInt(3)));
                }
            }
        }
        return rows;
    }

    public List<MemberCount> openLoansPerMember() throws SQLException {
        String sql = """
                SELECT m.id, m.name, COUNT(l.id)
                FROM members m
                LEFT JOIN loans l ON l.member_id = m.id AND l.returned IS NULL
                GROUP BY m.id, m.name
                ORDER BY m.id""";
        List<MemberCount> rows = new ArrayList<>();
        try (PreparedStatement query = conn.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                rows.add(new MemberCount(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
        }
        return rows;
    }

    // ---- Loading the model and saving changes ----

    /** Builds the task 09 model from the tables, not from the CSV files. */
    public Library loadLibrary() throws SQLException {
        Library library = new Library();
        try (Statement statement = conn.createStatement()) {
            try (ResultSet rs = statement.executeQuery(
                    "SELECT id, title, author, year, genre, copies, format, length FROM books ORDER BY id")) {
                while (rs.next()) {
                    String[] fields = new String[8];
                    for (int i = 0; i < fields.length; i++) {
                        fields[i] = rs.getString(i + 1);
                    }
                    library.addBook(Book.makeBook(fields));
                }
            }
            try (ResultSet rs = statement.executeQuery(
                    "SELECT id, name, email, type, joined FROM members ORDER BY id")) {
                while (rs.next()) {
                    library.addMember(new Member(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5)));
                }
            }
            try (ResultSet rs = statement.executeQuery(
                    "SELECT id, book_id, member_id, borrowed, returned FROM loans ORDER BY id")) {
                while (rs.next()) {
                    String returned = rs.getString(5);      // null when the column is NULL
                    library.addLoan(new Loan(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                            LocalDate.parse(rs.getString(4)), returned == null ? null : LocalDate.parse(returned)));
                }
            }
        }
        return library;
    }

    public void insertLoan(Loan loan) throws SQLException {
        try (PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO loans (id, book_id, member_id, borrowed, returned) VALUES (?, ?, ?, ?, NULL)")) {
            insert.setInt(1, loan.getId());
            insert.setInt(2, loan.getBookId());
            insert.setInt(3, loan.getMemberId());
            insert.setString(4, loan.getBorrowed().toString());
            insert.executeUpdate();
        }
    }

    public void markReturned(Loan loan) throws SQLException {
        LocalDate returned = loan.getReturned()
                .orElseThrow(() -> new IllegalArgumentException("loan has not been returned"));
        try (PreparedStatement update = conn.prepareStatement("UPDATE loans SET returned = ? WHERE id = ?")) {
            update.setString(1, returned.toString());
            update.setInt(2, loan.getId());
            update.executeUpdate();
        }
    }

    // ---- Stretch 1: a whole borrow inside one transaction ----

    /**
     * Inserts the loan first, then checks stock in SQL. If the check fails the
     * transaction rolls back and the insert disappears.
     * Returns the new loan id, or empty when the borrow was refused.
     */
    public Optional<Integer> borrowInTransaction(int memberId, int bookId, LocalDate today) throws SQLException {
        conn.setAutoCommit(false);
        try {
            int newId = queryInt("SELECT COALESCE(MAX(id), 0) + 1 FROM loans", null);
            try (PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO loans (id, book_id, member_id, borrowed, returned) VALUES (?, ?, ?, ?, NULL)")) {
                insert.setInt(1, newId);
                insert.setInt(2, bookId);
                insert.setInt(3, memberId);
                insert.setString(4, today.toString());
                insert.executeUpdate();
            }
            int out = queryInt("SELECT COUNT(*) FROM loans WHERE book_id = ? AND returned IS NULL", bookId);
            int copies = queryInt("SELECT copies FROM books WHERE id = ?", bookId);
            if (out > copies) {
                conn.rollback();        // the insert above is undone
                return Optional.empty();
            }
            conn.commit();
            return Optional.of(newId);
        } catch (SQLException error) {
            conn.rollback();
            throw error;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /** One whole number from a query with at most one ? parameter. */
    private int queryInt(String sql, Integer parameter) throws SQLException {
        try (PreparedStatement query = conn.prepareStatement(sql)) {
            if (parameter != null) {
                query.setInt(1, parameter);
            }
            try (ResultSet rs = query.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // ---- Stretch 2: an index ----

    /**
     * The index on loans(member_id) helps openLoansPerMember(): the LEFT JOIN
     * looks up each member's loans by member_id, and with the index SQLite finds
     * them directly instead of reading every row of loans for every member.
     */
    public void createMemberIndex() throws SQLException {
        try (Statement statement = conn.createStatement()) {
            statement.execute("CREATE INDEX IF NOT EXISTS idx_loans_member ON loans (member_id)");
        }
    }
}
