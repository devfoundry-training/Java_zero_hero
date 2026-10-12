import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Task 08 - storage: CSV and JSON in and out.
 *
 * Loading never crashes on bad data: a bad line is reported and skipped.
 * A missing file is different: it throws NoSuchFileException (an IOException)
 * and the caller decides what to do.
 * Every file is read and written as UTF-8 so it means the same on every machine.
 */
public final class Storage {

    static final List<String> BOOK_FIELDS = List.of("id", "title", "author", "year", "genre", "copies", "format", "length");
    static final List<String> MEMBER_FIELDS = List.of("id", "name", "email", "type", "joined");

    private Storage() {
        // static members only: no objects of this class
    }

    /**
     * Digits with an optional minus sign, nothing else. Integer.parseInt alone
     * would also accept "+4", which the brief does not count as a whole number.
     */
    static int wholeNumber(String text, String name) {
        if (!text.matches("-?[0-9]+")) {
            throw new IllegalArgumentException(name + " must be a whole number, got '" + text + "'");
        }
        return Integer.parseInt(text);
    }

    // ---- Reading CSV ----

    public static LoadResult loadBooksCsv(Path path) throws IOException {
        LoadResult result = new LoadResult();
        Set<Integer> seen = new TreeSet<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);    // \n or \r\n, both fine
        for (int i = 1; i < lines.size(); i++) {      // index 0 is the header
            int lineNumber = i + 1;                     // the header is line 1
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            String[] row = line.split(",", -1);         // -1 keeps empty fields at the end
            if (row.length != BOOK_FIELDS.size()) {
                result.skip(lineNumber, "expected " + BOOK_FIELDS.size() + " fields, found " + row.length);
                continue;
            }
            Book book;
            try {
                wholeNumber(row[0], "id");
                wholeNumber(row[3], "year");
                wholeNumber(row[5], "copies");
                wholeNumber(row[7], "length");
                book = Book.makeBook(row);              // checks the format, then the Book rules
            } catch (IllegalArgumentException error) {
                result.skip(lineNumber, error.getMessage());
                continue;
            }
            if (!seen.add(book.getId())) {              // add returns false when it was already there
                result.skip(lineNumber, "duplicate book id " + book.getId());
                continue;
            }
            result.books.add(book);
        }
        return result;
    }

    public static LoadResult loadMembersCsv(Path path) throws IOException {
        LoadResult result = new LoadResult();
        Set<Integer> seen = new TreeSet<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            String[] row = line.split(",", -1);
            if (row.length != MEMBER_FIELDS.size()) {
                result.skip(lineNumber, "expected " + MEMBER_FIELDS.size() + " fields, found " + row.length);
                continue;
            }
            Member member;
            try {
                member = new Member(wholeNumber(row[0], "id"), row[1], row[2], row[3], row[4]);
            } catch (IllegalArgumentException error) {
                result.skip(lineNumber, error.getMessage());
                continue;
            }
            if (!seen.add(member.getId())) {
                result.skip(lineNumber, "duplicate member id " + member.getId());
                continue;
            }
            result.members.add(member);
        }
        return result;
    }

    // ---- Writing CSV and JSON ----

    /** One book as a CSV line, in the column order of books.csv. */
    static String bookRow(Book book) {
        return book.getId() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.getYear() + ","
                + book.getGenre() + "," + book.getCopies() + "," + book.formatName() + "," + book.getLength();
    }

    public static void saveBooksCsv(Library library, Path path) throws IOException {
        StringBuilder text = new StringBuilder(String.join(",", BOOK_FIELDS)).append('\n');
        for (Book book : library.allBooks()) {
            text.append(bookRow(book)).append('\n');    // \n, not println: the same bytes on every system
        }
        Files.createDirectories(path.getParent());
        Files.writeString(path, text, StandardCharsets.UTF_8);
    }

    static String bookJson(Book book) {
        return "{\"id\": " + book.getId()
                + ", \"title\": " + Json.quote(book.getTitle())
                + ", \"author\": " + Json.quote(book.getAuthor())
                + ", \"year\": " + book.getYear()
                + ", \"genre\": " + Json.quote(book.getGenre())
                + ", \"copies\": " + book.getCopies()
                + ", \"format\": " + Json.quote(book.formatName())
                + ", \"length\": " + book.getLength() + "}";
    }

    static String memberJson(Member member) {
        List<String> loans = member.getLoans().stream().map(String::valueOf).toList();
        return "{\"id\": " + member.getId()
                + ", \"name\": " + Json.quote(member.getName())
                + ", \"email\": " + Json.quote(member.getEmail())
                + ", \"type\": " + Json.quote(member.getType())
                + ", \"joined\": " + Json.quote(member.getJoined())
                + ", \"loans\": [" + String.join(", ", loans) + "]}";
    }

    /** The whole library as JSON, written by hand: two-space indent, one object per line. */
    public static void saveJson(Library library, Path path) throws IOException {
        StringBuilder text = new StringBuilder("{\n  \"books\": [\n");
        List<Book> books = library.allBooks();
        for (int i = 0; i < books.size(); i++) {
            text.append("    ").append(bookJson(books.get(i))).append(i < books.size() - 1 ? ",\n" : "\n");
        }
        text.append("  ],\n  \"members\": [\n");
        List<Member> members = library.allMembers();
        for (int i = 0; i < members.size(); i++) {
            text.append("    ").append(memberJson(members.get(i))).append(i < members.size() - 1 ? ",\n" : "\n");
        }
        text.append("  ]\n}\n");
        Files.createDirectories(path.getParent());
        Files.writeString(path, text, StandardCharsets.UTF_8);
    }

    // ---- Stretch: reading the JSON back ----

    /** Throws NoSuchFileException when missing, IllegalArgumentException for broken JSON. */
    public static LoadResult loadJson(Path path) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8);
        Object document;
        try {
            document = Json.parse(text);
        } catch (IllegalArgumentException error) {
            throw new IllegalArgumentException("not valid JSON", error);
        }
        LoadResult result = new LoadResult();
        try {
            Map<?, ?> root = (Map<?, ?>) document;
            for (Object item : (List<?>) root.get("books")) {
                Map<?, ?> book = (Map<?, ?>) item;
                String[] fields = new String[BOOK_FIELDS.size()];
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = String.valueOf(book.get(BOOK_FIELDS.get(i)));
                }
                result.books.add(Book.makeBook(fields));
            }
            for (Object item : (List<?>) root.get("members")) {
                Map<?, ?> member = (Map<?, ?>) item;
                int id = ((Long) member.get("id")).intValue();
                result.members.add(new Member(id, (String) member.get("name"), (String) member.get("email"),
                        (String) member.get("type"), (String) member.get("joined")));
                for (Object bookId : (List<?>) member.get("loans")) {
                    result.loans.add(new LoadResult.SavedLoan(id, ((Long) bookId).intValue()));
                }
            }
        } catch (ClassCastException | NullPointerException error) {
            // A key is missing or holds the wrong kind of value
            throw new IllegalArgumentException("not a library file", error);
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
        // Saved loans go back through borrow(), so the copy counts and the
        // limits are checked exactly as if they were new loans
        for (LoadResult.SavedLoan loan : result.loans) {
            library.borrow(loan.memberId(), loan.bookId());
        }
        return library;
    }
}
