import java.util.List;
import java.util.Optional;

/**
 * The library (starter): it will hold the books and members, keyed by id, and
 * the borrowing rules.
 *
 * TODO Core 4: two maps keyed by id (a TreeMap keeps the ids in order) and the
 * methods below. Not found is Optional.empty(), never null.
 */
public class Library {

    public void addBook(Book book) {
        throw new UnsupportedOperationException("not implemented");
    }

    public void addMember(Member member) {
        throw new UnsupportedOperationException("not implemented");
    }

    public int bookCount() {
        throw new UnsupportedOperationException("not implemented");
    }

    public int memberCount() {
        throw new UnsupportedOperationException("not implemented");
    }

    public Optional<Book> findById(int bookId) {
        throw new UnsupportedOperationException("not implemented");
    }

    public Optional<Member> findMember(int memberId) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** Books whose title contains the text, ignoring case, by id. */
    public List<Book> findByTitle(String text) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** False if the member or book is unknown, or no copy is available. */
    public boolean borrow(int memberId, int bookId) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** False if the member or book is unknown, or the member does not have that book. */
    public boolean returnBook(int memberId, int bookId) {
        throw new UnsupportedOperationException("not implemented");
    }
}
