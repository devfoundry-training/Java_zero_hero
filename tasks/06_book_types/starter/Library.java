import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The library owns the books and members, keyed by id, and the borrowing rules.
 * A TreeMap keeps its keys in order, so every list comes out by id.
 */
public class Library {

    private final Map<Integer, Book> books = new TreeMap<>();
    private final Map<Integer, Member> members = new TreeMap<>();

    public void addBook(Book book) {
        books.put(book.getId(), book);
    }

    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    public int bookCount() {
        return books.size();
    }

    public int memberCount() {
        return members.size();
    }

    /** The book, or Optional.empty() when there is no such id. */
    public Optional<Book> findById(int bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    public Optional<Member> findMember(int memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    /** Books whose title contains the text, ignoring case, by id. */
    public List<Book> findByTitle(String text) {
        String wanted = text.toLowerCase(Locale.ROOT);
        List<Book> found = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase(Locale.ROOT).contains(wanted)) {
                found.add(book);
            }
        }
        return found;
    }

    public boolean borrow(int memberId, int bookId) {
        Member member = members.get(memberId);
        Book book = books.get(bookId);
        if (member == null || book == null) {
            return false;
        }
        if (!book.lend()) {
            return false;
        }
        member.addLoan(bookId);
        return true;
    }

    public boolean returnBook(int memberId, int bookId) {
        Member member = members.get(memberId);
        Book book = books.get(bookId);
        if (member == null || book == null || !member.hasLoan(bookId)) {
            return false;
        }
        member.removeLoan(bookId);
        book.takeBack();
        return true;
    }

    /** Every book, by id. */
    public List<Book> allBooks() {
        return new ArrayList<>(books.values());
    }

    /** Stretch 3: the books with at least one copy on the shelf. */
    public List<Book> availableBooks() {
        List<Book> found = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.available() > 0) {
                found.add(book);
            }
        }
        return found;
    }
}
