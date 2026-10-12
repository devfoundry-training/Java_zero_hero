import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The library from task 06. What changed:
 *   - borrow() and returnBook() throw a LibraryException subclass instead of
 *     returning false, so the caller learns WHY
 *   - book(id) and member(id) are like findById / findMember, but a missing
 *     id is an error rather than an empty Optional
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

    public List<Book> allBooks() {
        return new ArrayList<>(books.values());
    }

    public List<Member> allMembers() {
        return new ArrayList<>(members.values());
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

    /** The book with this id; a missing book is an error. */
    public Book book(int bookId) {
        return findById(bookId).orElseThrow(() -> new UnknownBookException(bookId));
    }

    public Member member(int memberId) {
        return findMember(memberId).orElseThrow(() -> new UnknownMemberException(memberId));
    }

    /** The order of the checks is part of the brief: member, book, limit, copies. */
    public void borrow(int memberId, int bookId) {
        Member member = member(memberId);
        Book book = book(bookId);
        int limit = member.getPolicy().maxLoans();
        if (!member.canBorrowMore()) {
            throw new MemberLimitReachedException(member.getName(), limit);
        }
        book.lend();        // throws BookNotAvailableException
        member.addLoan(bookId);
    }

    public void returnBook(int memberId, int bookId) {
        Member member = member(memberId);
        Book book = book(bookId);
        if (!member.hasLoan(bookId)) {
            throw new NotOnLoanException(book.getTitle(), member.getName());
        }
        member.removeLoan(bookId);
        book.takeBack();
    }

    // ---- TODO task 09: loans with dates (keep a Map of loans by id as well) ----

    /** TODO Core 2: add a loan from loans.csv; an open loan takes a copy off the shelf. */
    public void addLoan(Loan loan) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 3: borrowed plus the member's policy loan days. */
    public LocalDate dueDate(Loan loan) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 4: up to the return date, or up to today for an open loan; never below 0. */
    public int daysLate(Loan loan, LocalDate today) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 5: days late times the policy's fine per day, in pence. */
    public int fine(Loan loan, LocalDate today) {
        throw new UnsupportedOperationException("not implemented");
    }
}
