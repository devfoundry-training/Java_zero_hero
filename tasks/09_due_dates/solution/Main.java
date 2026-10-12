import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 09 - Loans with due dates (solution, Core + Stretch).
 *
 * Session 9: dates and times. Builds on task 08.
 * "Today" is an input, never the real clock, so the output is the same
 * whenever and wherever it runs.
 *
 * Run from the repo root (DATA_DIR defaults to tasks/data, TODAY to 2026-10-12):
 *   mvn -q compile exec:java@task -Dtask=09_due_dates/solution -Dexec.args="tasks/data"
 *   mvn -q compile exec:java@task -Dtask=09_due_dates/solution -Dexec.args="tasks/data 2026-10-12 --stretch"
 */
public class Main {

    static final LocalDate DEFAULT_TODAY = LocalDate.of(2026, 10, 12);

    public static void main(String[] args) throws IOException {
        List<String> positional = new ArrayList<>();
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                positional.add(arg);
            }
        }
        Path data = Path.of(positional.size() > 0 ? positional.get(0) : "tasks/data");
        LocalDate today = positional.size() > 1 ? LocalDate.parse(positional.get(1)) : DEFAULT_TODAY;

        Library library = Storage.loadAll(data);
        System.out.println("Today is " + today);        // LocalDate.toString() is always YYYY-MM-DD
        loansTable(library, today);
        overdueReport(library, today);
        finesReport(library, today);
        newLoans(library, today);

        if (List.of(args).contains("--stretch")) {
            printStretch(library, today);
        }
    }

    static void loansTable(Library library, LocalDate today) {
        System.out.println("Loans");
        System.out.println(String.format("%2s  %-30s  %-12s  %-10s  %-10s  %s",
                "ID", "Book", "Member", "Borrowed", "Due", "Status"));
        System.out.println(String.join("  ", "-".repeat(2), "-".repeat(30), "-".repeat(12),
                "-".repeat(10), "-".repeat(10), "-".repeat(6)));
        for (Loan loan : library.allLoans()) {
            Book book = library.book(loan.getBookId());
            Member member = library.member(loan.getMemberId());
            System.out.println(String.format("%2d  %-30s  %-12s  %s  %s  %s", loan.getId(), book.getTitle(),
                    member.getName(), loan.getBorrowed(), library.dueDate(loan), library.status(loan, today)));
        }
    }

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

    static void finesReport(Library library, LocalDate today) {
        int onOverdue = 0;
        int onReturns = 0;
        for (Loan loan : library.allLoans()) {
            if (loan.isOpen()) {
                onOverdue += library.fine(loan, today);
            } else {
                onReturns += library.fine(loan, today);
            }
        }
        System.out.println("Fines");
        System.out.println("on overdue loans: " + Text.money(onOverdue));
        System.out.println("on late returns: " + Text.money(onReturns));
    }

    static void newLoans(Library library, LocalDate today) {
        System.out.println("New loans on " + today);
        tryBorrow(library, 105, 15, today);
        tryBorrow(library, 105, 16, today);
        try {
            Loan loan = library.returnBook(102, 14, today);
            int late = library.daysLate(loan, today);
            String tail = late == 0 ? "on time"
                    : Text.plural(late, "day", "days") + " late, fine " + Text.money(library.fine(loan, today));
            System.out.println("OK: " + library.member(102).getName() + " returned "
                    + library.book(14).getTitle() + ", " + tail);
        } catch (LibraryException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    static void tryBorrow(Library library, int memberId, int bookId, LocalDate today) {
        try {
            Loan loan = library.borrow(memberId, bookId, today);
            System.out.println("OK: " + library.member(memberId).getName() + " borrowed "
                    + library.book(bookId).getTitle() + ", due " + library.dueDate(loan));
        } catch (LibraryException error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    // ---- Stretch: closed on Sundays, and what is due this week ----

    /** The library is closed on Sundays: a Sunday due date moves to the Monday. */
    static LocalDate openDay(LocalDate day) {
        return day.getDayOfWeek() == DayOfWeek.SUNDAY ? day.plusDays(1) : day;
    }

    static void printStretch(Library library, LocalDate today) {
        System.out.println("Stretch");
        for (Loan loan : library.allLoans()) {
            LocalDate due = library.dueDate(loan);
            if (!openDay(due).equals(due)) {
                System.out.println("loan " + loan.getId() + ": due " + due + " is a Sunday, moves to " + openDay(due));
            }
        }
        LocalDate weekEnd = today.plusDays(7);
        List<String> dueSoon = new ArrayList<>();
        for (Loan loan : library.allLoans()) {
            LocalDate due = library.dueDate(loan);
            if (loan.isOpen() && !due.isBefore(today) && !due.isAfter(weekEnd)) {    // today to today + 7, inclusive
                dueSoon.add(library.book(loan.getBookId()).getTitle());
            }
        }
        System.out.println("Due by " + weekEnd + ": " + String.join(", ", dueSoon));
    }
}
