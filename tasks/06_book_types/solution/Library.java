import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The library from task 05. borrow() now also asks the member's policy
 * whether they may take another book.
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

    public Optional<Book> findById(int bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    public Optional<Member> findMember(int memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

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

    /** Every book, by id. */
    public List<Book> allBooks() {
        return new ArrayList<>(books.values());
    }

    public boolean borrow(int memberId, int bookId) {
        Member member = members.get(memberId);
        Book book = books.get(bookId);
        if (member == null || book == null) {
            return false;
        }
        if (!member.canBorrowMore()) {      // the policy decides the limit
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
}
