import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Task 09 - storage: the task 08 CSV loaders plus loans.csv.
 *
 * Saving is unchanged from task 08 and left out here. Dates are text in the
 * files (YYYY-MM-DD) and LocalDate in the program; the conversion happens
 * here and nowhere else.
 */
public final class Storage {

    static final List<String> BOOK_FIELDS = List.of("id", "title", "author", "year", "genre", "copies", "format", "length");
    static final List<String> MEMBER_FIELDS = List.of("id", "name", "email", "type", "joined");
    static final List<String> LOAN_FIELDS = List.of("id", "book_id", "member_id", "borrowed", "returned");

    private Storage() {
        // static members only: no objects of this class
    }

    /** One data line of a CSV file and its line number (the header is line 1). */
    record Row(int lineNumber, String[] fields) {
    }

    static int wholeNumber(String text, String name) {
        if (!text.matches("-?[0-9]+")) {
            throw new IllegalArgumentException(name + " must be a whole number, got '" + text + "'");
        }
        return Integer.parseInt(text);
    }

    /** "2026-10-12" becomes a LocalDate; anything else is a bad line. */
    static LocalDate parseDate(String text, String name) {
        try {
            return LocalDate.parse(text);       // ISO format, YYYY-MM-DD
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException(name + " must be a date YYYY-MM-DD, got '" + text + "'", error);
        }
    }

    /**
     * Every data row with the right number of fields. Blank lines are skipped
     * silently; rows with the wrong width are reported and skipped.
     * A missing file throws NoSuchFileException.
     */
    static List<Row> readRows(Path path, int width, LoadResult result) throws IOException {
        List<Row> rows = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            String[] fields = line.split(",", -1);
            if (fields.length != width) {
                result.skip(i + 1, "expected " + width + " fields, found " + fields.length);
                continue;
            }
            rows.add(new Row(i + 1, fields));
        }
        return rows;
    }

    public static LoadResult loadBooksCsv(Path path, LoadResult result) throws IOException {
        Set<Integer> seen = new HashSet<>();
        for (Row row : readRows(path, BOOK_FIELDS.size(), result)) {
            String[] f = row.fields();
            Book book;
            try {
                wholeNumber(f[0], "id");
                wholeNumber(f[3], "year");
                wholeNumber(f[5], "copies");
                wholeNumber(f[7], "length");
                book = Book.makeBook(f);
            } catch (IllegalArgumentException error) {
                result.skip(row.lineNumber(), error.getMessage());
                continue;
            }
            if (!seen.add(book.getId())) {
                result.skip(row.lineNumber(), "duplicate book id " + book.getId());
                continue;
            }
            result.books.add(book);
        }
        return result;
    }

    public static LoadResult loadBooksCsv(Path path) throws IOException {
        return loadBooksCsv(path, new LoadResult());
    }

    public static LoadResult loadMembersCsv(Path path, LoadResult result) throws IOException {
        for (Row row : readRows(path, MEMBER_FIELDS.size(), result)) {
            String[] f = row.fields();
            try {
                parseDate(f[4], "joined");          // checked here, kept as text in Member
                result.members.add(new Member(wholeNumber(f[0], "id"), f[1], f[2], f[3], f[4]));
            } catch (IllegalArgumentException error) {
                result.skip(row.lineNumber(), error.getMessage());
            }
        }
        return result;
    }

    public static LoadResult loadLoansCsv(Path path, LoadResult result) throws IOException {
        for (Row row : readRows(path, LOAN_FIELDS.size(), result)) {
            String[] f = row.fields();
            try {
                LocalDate returned = f[4].isEmpty() ? null : parseDate(f[4], "returned");    // optional date
                result.loans.add(new Loan(wholeNumber(f[0], "id"), wholeNumber(f[1], "book_id"),
                        wholeNumber(f[2], "member_id"), parseDate(f[3], "borrowed"), returned));
            } catch (IllegalArgumentException error) {
                result.skip(row.lineNumber(), error.getMessage());
            }
        }
        return result;
    }

    public static Library buildLibrary(LoadResult result) {
        Library library = new Library();
        for (Book book : result.books) {
            library.addBook(book);
        }
        for (Member member : result.members) {
            library.addMember(member);
        }
        for (Loan loan : result.loans) {
            library.addLoan(loan);
        }
        return library;
    }

    /** books.csv, members.csv and loans.csv from one folder. */
    public static Library loadAll(Path data) throws IOException {
        LoadResult result = new LoadResult();
        loadBooksCsv(data.resolve("books.csv"), result);
        loadMembersCsv(data.resolve("members.csv"), result);
        loadLoansCsv(data.resolve("loans.csv"), result);
        return buildLibrary(result);
    }
}
