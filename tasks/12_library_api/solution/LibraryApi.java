import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Task 12 - the API without the HTTP: simple values in, (status, JSON text) out.
 *
 * Keeping this apart from the server makes it easy to test and to read: the
 * server only turns requests into calls here and the answers into responses.
 *
 * The server answers requests on several threads at once, so every method that
 * reads or changes the library is synchronized: one request at a time sees and
 * changes it.
 */
public class LibraryApi {

    static final String BAD_BODY = "body must be a JSON object with whole-number member_id and book_id";

    /** What the server sends back. */
    record Response(int status, String json) {
    }

    private final Library library;
    private final LocalDate today;

    public LibraryApi(Library library, LocalDate today) {
        this.library = library;
        this.today = today;
    }

    static Response error(int status, String message) {
        return new Response(status, Json.write(Map.of("error", message)));
    }

    /** A book as a JSON object. LinkedHashMap keeps the keys in this order. */
    private Map<String, Object> bookJson(Book book) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("id", book.getId());
        json.put("title", book.getTitle());
        json.put("author", book.getAuthor());
        json.put("year", book.getYear());
        json.put("genre", book.getGenre());
        json.put("format", book.formatName());
        json.put("copies", book.getCopies());
        json.put("available", library.freeCopies(book.getId()));    // copies held for a reservation do not count
        return json;
    }

    /** GET /books and GET /books?title=TEXT */
    public synchronized Response listBooks(String title) {
        List<Book> books = title == null || title.isEmpty() ? library.allBooks() : library.findByTitle(title);
        List<Object> json = new ArrayList<>();
        for (Book book : books) {
            json.add(bookJson(book));
        }
        return new Response(200, Json.write(json));
    }

    /** Stretch: GET /books/ID */
    public synchronized Response getBook(int bookId) {
        Optional<Book> book = library.findById(bookId);
        if (book.isEmpty()) {
            return error(404, "no book with id " + bookId);
        }
        return new Response(200, Json.write(bookJson(book.get())));
    }

    /** POST /loans with {"member_id": 101, "book_id": 4} */
    public Response createLoan(byte[] body) {
        Map<?, ?> request;
        try {
            Object parsed = Json.parse(new String(body, StandardCharsets.UTF_8));
            if (!(parsed instanceof Map<?, ?> map)) {
                return error(400, BAD_BODY);
            }
            request = map;
        } catch (IllegalArgumentException notJson) {
            return error(400, BAD_BODY);
        }
        // Whole numbers only: the parser gives Long for 101, String for "101", Double for 101.0
        Integer memberId = wholeNumber(request.get("member_id"));
        Integer bookId = wholeNumber(request.get("book_id"));
        if (memberId == null || bookId == null) {
            return error(400, BAD_BODY);
        }
        synchronized (this) {
            try {
                Loan loan = library.borrow(memberId, bookId, today);
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("id", loan.getId());
                json.put("book_id", loan.getBookId());
                json.put("member_id", loan.getMemberId());
                json.put("borrowed", loan.getBorrowed().toString());
                json.put("due", library.dueDate(loan).toString());
                return new Response(201, Json.write(json));
            } catch (UnknownMemberException | UnknownBookException missing) {
                return error(404, missing.getMessage());
            } catch (LibraryException refused) {
                return error(409, refused.getMessage());     // a rule said no: a conflict
            }
        }
    }

    /** The value as an int if it is a JSON whole number that fits, otherwise null. */
    private static Integer wholeNumber(Object value) {
        if (value instanceof Long number && number >= Integer.MIN_VALUE && number <= Integer.MAX_VALUE) {
            return number.intValue();
        }
        return null;
    }

    /** Stretch: GET /books/ID/openlibrary */
    public Response openLibrary(int bookId) {
        Optional<Book> book;
        synchronized (this) {
            book = library.findById(bookId);
        }
        if (book.isEmpty()) {
            return error(404, "no book with id " + bookId);
        }
        // The slow network call happens outside the lock, so other requests carry on meanwhile
        Map<String, Object> match;
        try {
            match = OpenLibrary.firstMatch(OpenLibrary.search(book.get().getTitle(), 1));
        } catch (IOException | IllegalArgumentException failed) {
            return error(502, "Open Library did not answer");
        } catch (InterruptedException stopped) {
            Thread.currentThread().interrupt();
            return error(502, "Open Library did not answer");
        }
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("id", book.get().getId());
        json.put("title", book.get().getTitle());
        json.put("open_library", match);       // null when there are no results
        return new Response(200, Json.write(json));
    }
}
