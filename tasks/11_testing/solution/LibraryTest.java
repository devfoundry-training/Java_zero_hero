import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Task 11 - Test the rules (solution, Core + Stretch).
 *
 * Session 11: testing. Every test gets a fresh library from @BeforeEach, so no
 * test depends on another one or on the order they run in. Dates are fixed
 * values: nothing reads the real clock.
 *
 * Run from the repo root (the console launcher prints every test name):
 *   mvn -q compile exec:java@tests -Dtask=11_testing/solution
 */
class LibraryTest {

    static final LocalDate TODAY = LocalDate.of(2026, 10, 12);

    // The fixture: books 1 to 8 of books.csv and three members, no loans
    static final List<String> FIXTURE_BOOKS = List.of(
            "1,Pride and Prejudice,Jane Austen,1813,Classic,3,print,432",
            "2,Nineteen Eighty-Four,George Orwell,1949,Dystopian,2,print,328",
            "3,The Hobbit,J. R. R. Tolkien,1937,Fantasy,0,print,310",
            "4,Dune,Frank Herbert,1965,Science Fiction,4,print,412",
            "5,Robinson Crusoe,Daniel Defoe,1719,Adventure,1,print,320",
            "6,The Time Machine,H. G. Wells,1895,Science Fiction,2,ebook,118",
            "7,Animal Farm,George Orwell,1945,Dystopian,5,print,112",
            "8,Emma,Jane Austen,1815,Classic,1,audio,1150");

    static final int AMIRA = 101;   // standard
    static final int BEN = 102;     // student
    static final int CHLOE = 103;   // staff

    static Library fixture() {
        Library library = new Library();
        for (String line : FIXTURE_BOOKS) {
            library.addBook(Book.makeBook(line.split(",")));
        }
        library.addMember(new Member(AMIRA, "Amira Khan", "amira.khan@example.com", "standard", "2023-01-15"));
        library.addMember(new Member(BEN, "Ben Taylor", "ben.taylor@example.com", "student", "2024-09-02"));
        library.addMember(new Member(CHLOE, "Chloe Evans", "chloe.evans@example.com", "staff", "2019-05-20"));
        return library;
    }

    Library library;

    @BeforeEach
    void setUp() {
        library = fixture();
    }

    // ---- Borrowing ----

    @Test
    void borrowReducesAvailable() {
        library.borrow(AMIRA, 4, TODAY);
        assertEquals(3, library.book(4).available());
    }

    @Test
    void returnRestoresAvailable() {
        library.borrow(AMIRA, 4, TODAY);
        library.returnBook(AMIRA, 4, TODAY);
        assertEquals(4, library.book(4).available());
    }

    @Test
    void borrowUnknownBookRaises() {
        assertThrows(UnknownBookException.class, () -> library.borrow(AMIRA, 99, TODAY));
    }

    @Test
    void borrowUnknownMemberRaises() {
        assertThrows(UnknownMemberException.class, () -> library.borrow(999, 1, TODAY));
    }

    @Test
    void borrowWithNoCopiesRaises() {
        assertThrows(BookNotAvailableException.class, () -> library.borrow(AMIRA, 3, TODAY));
    }

    @Test
    void returnBookNotOnLoanRaises() {
        assertThrows(NotOnLoanException.class, () -> library.returnBook(AMIRA, 4, TODAY));
    }

    @Test
    void standardLimitIsThreeLoans() {
        for (int bookId : new int[] {1, 2, 4}) {
            library.borrow(AMIRA, bookId, TODAY);
        }
        assertThrows(MemberLimitReachedException.class, () -> library.borrow(AMIRA, 7, TODAY));
    }

    @Test
    void studentLimitIsFiveLoans() {
        for (int bookId : new int[] {1, 2, 4, 6, 7}) {
            library.borrow(BEN, bookId, TODAY);
        }
        assertThrows(MemberLimitReachedException.class, () -> library.borrow(BEN, 8, TODAY));
    }

    // ---- Dates and fines: each test builds a loan directly with the dates it needs ----

    LocalDate due(int memberId, LocalDate borrowed) {
        return library.dueDate(new Loan(1, 1, memberId, borrowed));
    }

    @Test
    void dueDateStandardIs14Days() {
        assertEquals(LocalDate.of(2026, 10, 26), due(AMIRA, LocalDate.of(2026, 10, 12)));
    }

    @Test
    void dueDateStudentIs21Days() {
        assertEquals(LocalDate.of(2026, 11, 2), due(BEN, LocalDate.of(2026, 10, 12)));
    }

    @Test
    void dueDateStaffIs28Days() {
        assertEquals(LocalDate.of(2026, 11, 9), due(CHLOE, LocalDate.of(2026, 10, 12)));
    }

    @Test
    void dueDateCrossesMonthAndYearEnd() {
        assertEquals(LocalDate.of(2027, 1, 10), due(BEN, LocalDate.of(2026, 12, 20)));
    }

    @Test
    void dueDateInLeapYear() {
        assertEquals(LocalDate.of(2028, 3, 5), due(AMIRA, LocalDate.of(2028, 2, 20)));
    }

    @Test
    void notOverdueOnDueDate() {
        Loan loan = new Loan(1, 1, AMIRA, LocalDate.of(2026, 9, 28));     // due 2026-10-12
        assertEquals(0, library.daysLate(loan, TODAY));
        assertEquals(0, library.fine(loan, TODAY));
    }

    @Test
    void fineOneDayLateStandard() {
        Loan loan = new Loan(1, 1, AMIRA, LocalDate.of(2026, 9, 27));     // due 2026-10-11
        assertEquals(25, library.fine(loan, TODAY));
    }

    @Test
    void fineTenDaysLateStudent() {
        Loan loan = new Loan(1, 1, BEN, LocalDate.of(2026, 9, 11));       // due 2026-10-02
        assertEquals(100, library.fine(loan, TODAY));
    }

    @Test
    void staffNeverFined() {
        Loan loan = new Loan(1, 1, CHLOE, LocalDate.of(2026, 1, 1));
        assertEquals(0, library.fine(loan, TODAY));
    }

    @Test
    void lateReturnFineUsesReturnDate() {
        // Due 2026-10-01, back 2026-10-05: 4 days late, whatever today is
        Loan loan = new Loan(1, 1, AMIRA, LocalDate.of(2026, 9, 17), LocalDate.of(2026, 10, 5));
        assertEquals(100, library.fine(loan, LocalDate.of(2026, 12, 25)));
    }

    @Test
    void formatMoney() {
        assertEquals("£0.00", Text.money(0));
        assertEquals("£0.05", Text.money(5));
        assertEquals("£1.25", Text.money(125));
        assertEquals("£10.00", Text.money(1000));
    }

    // ---- Reservations: written first, watched fail, then made to pass (TDD) ----

    @Nested
    class Reservations {

        @BeforeEach
        void borrowTheOnlyCopy() {
            library.borrow(AMIRA, 5, TODAY);        // the only copy of Robinson Crusoe
        }

        @Test
        void reserveBookWithNoFreeCopiesJoinsQueue() {
            assertEquals(1, library.reserve(BEN, 5));
            assertEquals(2, library.reserve(CHLOE, 5));
        }

        @Test
        void reserveBookWithFreeCopiesRaises() {
            assertThrows(ReservationNotNeededException.class, () -> library.reserve(BEN, 1));
        }

        @Test
        void reserveSameBookTwiceRaises() {
            library.reserve(BEN, 5);
            assertThrows(AlreadyReservedException.class, () -> library.reserve(BEN, 5));
        }

        @Test
        void returnedCopyIsHeldForFirstInQueue() {
            library.reserve(BEN, 5);
            library.returnBook(AMIRA, 5, TODAY);
            assertThrows(BookNotAvailableException.class, () -> library.borrow(CHLOE, 5, TODAY));
        }

        @Test
        void firstInQueueCanBorrowHeldCopy() {
            library.reserve(BEN, 5);
            library.returnBook(AMIRA, 5, TODAY);
            library.borrow(BEN, 5, TODAY);
            assertEquals(0, library.book(5).available());
            assertEquals(List.of(), library.heldFor(5));
        }

        @Test
        void queueIsFirstComeFirstServed() {
            library.reserve(CHLOE, 5);
            library.reserve(BEN, 5);
            library.returnBook(AMIRA, 5, TODAY);
            assertEquals(List.of(CHLOE), library.heldFor(5));
            assertEquals(List.of(BEN), library.queueFor(5));
        }
    }

    // ---- Stretch: the file loader, and a table of cases ----

    /** Walks up from the current folder to the first one holding the task data. */
    static Path findDataDir() {
        Path folder = Path.of("").toAbsolutePath();
        while (folder != null) {
            for (Path candidate : List.of(folder.resolve("tasks").resolve("data"), folder.resolve("data"))) {
                if (Files.exists(candidate.resolve("books_corrupt.csv"))) {
                    return candidate;
                }
            }
            folder = folder.getParent();
        }
        throw new IllegalStateException("could not find the data folder");
    }

    @Test
    void corruptCsvLoadsGoodLinesOnly() throws IOException {
        LoadResult result = Storage.loadBooksCsv(findDataDir().resolve("books_corrupt.csv"));
        List<Integer> ids = new ArrayList<>();
        for (Book book : result.books) {
            ids.add(book.getId());
        }
        assertEquals(List.of(1, 2, 4, 6, 10), ids);
        assertEquals(5, result.skipped);
    }

    @ParameterizedTest(name = "member {0}, days late {1}, fine {2}p")
    @CsvSource({
            "101, 0, 0",
            "101, 1, 25",
            "101, 20, 500",
            "102, 1, 10",
            "102, 30, 300",
            "103, 365, 0",
    })
    void finesTable(int memberId, int daysLate, int pence) {
        assertEquals(pence, library.member(memberId).getPolicy().fineFor(daysLate));
    }
}
