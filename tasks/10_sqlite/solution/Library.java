import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The library, now with dated loans. What changed from task 08:
 *   - a Loan records who borrowed what, when, and when it came back
 *   - borrow() and returnBook() take the date as a parameter, so nothing
 *     here ever reads the real clock (and the tests in task 11 stay stable)
 *   - due dates, days late and fines (in whole pence)
 */
public class Library {

    private final Map<Integer, Book> books = new TreeMap<>();
    private final Map<Integer, Member> members = new TreeMap<>();
    private final TreeMap<Integer, Loan> loans = new TreeMap<>();   // TreeMap for lastKey()

    // ---- Adding and finding ----

    public void addBook(Book book) {
        books.put(book.getId(), book);
    }

    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    /**
     * Adds a loan from the records. An open loan takes a copy off the shelf;
     * limits are not checked (history is what it is).
     */
    public void addLoan(Loan loan) {
        if (loan.isOpen()) {
            book(loan.getBookId()).lend();
            member(loan.getMemberId()).addLoan(loan.getBookId());
        }
        loans.put(loan.getId(), loan);
    }

    public int bookCount() {
        return books.size();
    }

    public int memberCount() {
        return members.size();
    }

    public int loanCount() {
        return loans.size();
    }

    public List<Book> allBooks() {
        return new ArrayList<>(books.values());
    }

    public List<Member> allMembers() {
        return new ArrayList<>(members.values());
    }

    /** Every loan, by id. */
    public List<Loan> allLoans() {
        return new ArrayList<>(loans.values());
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

    public Book book(int bookId) {
        return findById(bookId).orElseThrow(() -> new UnknownBookException(bookId));
    }

    public Member member(int memberId) {
        return findMember(memberId).orElseThrow(() -> new UnknownMemberException(memberId));
    }

    // ---- Dates and money ----

    public LocalDate dueDate(Loan loan) {
        return loan.getBorrowed().plusDays(member(loan.getMemberId()).getPolicy().loanDays());
    }

    /**
     * Whole days past the due date: up to the return date if the book is back,
     * otherwise up to today. Never negative.
     */
    public int daysLate(Loan loan, LocalDate today) {
        LocalDate end = loan.getReturned().orElse(today);
        long days = ChronoUnit.DAYS.between(dueDate(loan), end);    // negative when end is before the due date
        return (int) Math.max(0, days);
    }

    /** The fine in pence. */
    public int fine(Loan loan, LocalDate today) {
        return member(loan.getMemberId()).getPolicy().fineFor(daysLate(loan, today));
    }

    public String status(Loan loan, LocalDate today) {
        LocalDate due = dueDate(loan);
        if (!loan.isOpen()) {
            int late = daysLate(loan, today);
            return late == 0 ? "returned on time" : "returned " + Text.plural(late, "day", "days") + " late";
        }
        if (today.isBefore(due)) {
            return "on loan, " + Text.plural((int) ChronoUnit.DAYS.between(today, due), "day", "days") + " left";
        }
        if (today.isEqual(due)) {
            return "due today";
        }
        return "overdue by " + Text.plural((int) ChronoUnit.DAYS.between(due, today), "day", "days");
    }

    /** Open loans past their due date: most days late first, then by loan id. */
    public List<Loan> overdue(LocalDate today) {
        List<Loan> late = new ArrayList<>();
        for (Loan loan : loans.values()) {
            if (loan.isOpen() && daysLate(loan, today) > 0) {
                late.add(loan);
            }
        }
        late.sort(Comparator.comparingInt((Loan loan) -> -daysLate(loan, today)).thenComparingInt(Loan::getId));
        return late;
    }

    // ---- The rules ----

    /** Member, book, limit, copies: the same checks in the same order as task 07. */
    public Loan borrow(int memberId, int bookId, LocalDate today) {
        Member member = member(memberId);
        Book book = book(bookId);
        if (!member.canBorrowMore()) {
            throw new MemberLimitReachedException(member.getName(), member.getPolicy().maxLoans());
        }
        book.lend();
        member.addLoan(bookId);
        int newId = loans.isEmpty() ? 1 : loans.lastKey() + 1;     // the highest loan id plus 1
        Loan loan = new Loan(newId, bookId, memberId, today);
        loans.put(loan.getId(), loan);
        return loan;
    }

    public Loan returnBook(int memberId, int bookId, LocalDate today) {
        Member member = member(memberId);
        Book book = book(bookId);
        Loan open = null;
        for (Loan loan : loans.values()) {          // by id, so the oldest open loan comes back first
            if (loan.isOpen() && loan.getMemberId() == memberId && loan.getBookId() == bookId) {
                open = loan;
                break;
            }
        }
        if (open == null) {
            throw new NotOnLoanException(book.getTitle(), member.getName());
        }
        open.markReturned(today);
        member.removeLoan(bookId);
        book.takeBack();
        return open;
    }
}
