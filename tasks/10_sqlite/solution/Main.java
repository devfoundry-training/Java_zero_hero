import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Task 10 - SQLite storage (solution, Core + Stretch).
 *
 * Session 10: databases. Builds on task 09: the CSV files are imported once into
 * out/library.db (rebuilt on every run from data/schema.sql), and from then on
 * the database is the store. The task 09 classes still make every decision.
 *
 * Run from the repo root (DATA_DIR defaults to tasks/data, TODAY to 2026-10-12):
 *   mvn -q compile exec:java@task -Dtask=10_sqlite/solution -Dexec.args="tasks/data"
 *   mvn -q compile exec:java@task -Dtask=10_sqlite/solution -Dexec.args="tasks/data 2026-10-12 --stretch"
 */
public class Main {

    static final LocalDate DEFAULT_TODAY = LocalDate.of(2026, 10, 12);
    static final Path DB_PATH = Path.of("out", "library.db");

    public static void main(String[] args) throws IOException, SQLException {
        List<String> positional = new ArrayList<>();
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                positional.add(arg);
            }
        }
        Path data = Path.of(positional.size() > 0 ? positional.get(0) : "tasks/data");
        LocalDate today = positional.size() > 1 ? LocalDate.parse(positional.get(1)) : DEFAULT_TODAY;

        // ---- Core 1: a fresh database from schema.sql, filled from the CSV files ----
        Files.createDirectories(DB_PATH.getParent());
        Files.deleteIfExists(DB_PATH);      // start clean, so every run is the same
        try (LibraryDb db = new LibraryDb(DB_PATH)) {
            db.create(data.resolve("schema.sql"));
            System.out.println("Created out/library.db from schema.sql");
            LoadResult rows = new LoadResult();
            Storage.loadBooksCsv(data.resolve("books.csv"), rows);
            Storage.loadMembersCsv(data.resolve("members.csv"), rows);
            Storage.loadLoansCsv(data.resolve("loans.csv"), rows);
            db.importRows(rows);
            System.out.println("Imported " + db.count("books") + " books, " + db.count("members") + " members, "
                    + db.count("loans") + " loans");

            // ---- Core 2: questions answered in SQL ----
            System.out.println("Books per genre");
            for (LibraryDb.GenreCount row : db.genreCounts()) {
                System.out.println(row.genre() + ": " + row.count());
            }

            System.out.println("Nothing on the shelf");
            for (LibraryDb.ShelfRow row : db.unavailableBooks()) {
                System.out.println("#" + row.id() + " " + row.title() + ": " + row.available() + " of "
                        + row.copies() + " available");
            }

            for (String text : List.of("the", "x' OR '1'='1")) {
                List<LibraryDb.TitleRow> found = db.searchTitles(text);
                System.out.println("Search \"" + text + "\": " + Text.plural(found.size(), "book", "books"));
                for (LibraryDb.TitleRow row : found) {
                    System.out.println("  #" + row.id() + " " + row.title() + " (" + row.year() + ")");
                }
            }

            System.out.println("Open loans per member");
            for (LibraryDb.MemberCount row : db.openLoansPerMember()) {
                System.out.println("#" + row.id() + " " + row.name() + ": " + row.openLoans());
            }

            // ---- Core 3: the task 09 rules decide, the database remembers ----
            Library library = db.loadLibrary();
            System.out.println("New loans on " + today);
            for (int[] borrow : new int[][] {{105, 15}, {105, 16}}) {
                try {
                    Loan loan = library.borrow(borrow[0], borrow[1], today);
                    db.insertLoan(loan);
                    System.out.println("OK: " + library.member(borrow[0]).getName() + " borrowed "
                            + library.book(borrow[1]).getTitle() + ", due " + library.dueDate(loan)
                            + ", saved as loan " + loan.getId());
                } catch (LibraryException error) {
                    System.out.println("Error: " + error.getMessage());
                }
            }
            try {
                Loan loan = library.returnBook(102, 14, today);
                db.markReturned(loan);
                System.out.println("OK: " + library.member(102).getName() + " returned "
                        + library.book(14).getTitle() + ", fine " + Text.money(library.fine(loan, today)) + ", saved");
            } catch (LibraryException error) {
                System.out.println("Error: " + error.getMessage());
            }

            // Read everything back from the database to prove it was saved
            Library reloaded = db.loadLibrary();
            int open = 0;
            for (Loan loan : reloaded.allLoans()) {
                if (loan.isOpen()) {
                    open++;
                }
            }
            System.out.println("Reloaded: " + reloaded.bookCount() + " books, " + reloaded.memberCount()
                    + " members, " + reloaded.loanCount() + " loans (" + open + " open)");
            overdueReport(reloaded, today);

            if (List.of(args).contains("--stretch")) {
                printStretch(db, today);
            }
        }
    }

    /** Exactly the task 09 report, now fed from the database. */
    static void overdueReport(Library library, LocalDate today) {
        List<Loan> overdue = library.overdue(today);
        System.out.println("Overdue on " + today + ": " + Text.plural(overdue.size(), "loan", "loans"));
        for (Loan loan : overdue) {
            Book book = library.book(loan.getBookId());
            Member member = library.member(loan.getMemberId());
            System.out.println(Text.plural(library.daysLate(loan, today), "day", "days") + ": " + book.getTitle()
                    + ", " + member.getName() + " (" + member.getType() + "), due " + library.dueDate(loan)
                    + ", fine " + Text.money(library.fine(loan, today)));
        }
    }

    // ---- Stretch: a borrow that rolls back, and an index ----

    static void printStretch(LibraryDb db, LocalDate today) throws SQLException {
        System.out.println("Stretch");
        int before = db.count("loans");
        Optional<Integer> refused = db.borrowInTransaction(101, 3, today);
        System.out.println("borrow 101 3 in a transaction: " + (refused.isEmpty() ? "refused, rolled back" : "OK"));
        System.out.println("loans before " + before + ", after " + db.count("loans"));
        Optional<Integer> saved = db.borrowInTransaction(101, 4, today);
        System.out.println("borrow 101 4 in a transaction: saved as loan " + saved.orElseThrow());
        db.createMemberIndex();
        System.out.println("index on loans(member_id) created");
    }
}
