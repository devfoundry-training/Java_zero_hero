import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Task 09 - Loans with due dates (starter).
 *
 * The library classes, exceptions and Storage are the task 08 solution, with
 * stubs added for the new work: Loan.java, Library.addLoan / dueDate / daysLate /
 * fine and Storage.loadLoansCsv. Fill in each part marked TODO, then run the
 * check (see tasks/README.md). Never read the real clock: today is an argument.
 *
 * Run from the repo root (DATA_DIR defaults to tasks/data, TODAY to 2026-10-12):
 *   mvn -q compile exec:java@task -Dtask=09_due_dates/starter -Dexec.args="tasks/data"
 */
public class Main {

    static final LocalDate DEFAULT_TODAY = LocalDate.of(2026, 10, 12);

    public static void main(String[] args) throws IOException {
        Path data = Path.of(args.length > 0 ? args[0] : "tasks/data");
        LocalDate today = args.length > 1 ? LocalDate.parse(args[1]) : DEFAULT_TODAY;
        // TODO Core 2: load books.csv, members.csv and loans.csv
        // TODO Core 6: the loans table, the overdue report and the fines totals
        // TODO Core 7: 105 borrows 15, 105 borrows 16, 102 returns 14 (today)
        System.out.println("Core 2 to 7: not implemented");
    }
}
