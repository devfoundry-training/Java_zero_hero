import java.time.LocalDate;

/**
 * Task 12 - the API without the HTTP (starter): simple values in, (status, JSON text) out.
 *
 * Keep this apart from the server code: the server only turns requests into
 * calls here and the answers into responses. Json.java (the task 08 Stretch
 * parser) reads the POST body.
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

    /** TODO Core 2: GET /books and GET /books?title=TEXT, 200 and a JSON array of books by id. */
    public Response listBooks(String title) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 3 and 4: POST /loans, 201 and the new loan (borrowed today), or 400 / 404 / 409. */
    public Response createLoan(byte[] body) {
        throw new UnsupportedOperationException("not implemented");
    }
}
