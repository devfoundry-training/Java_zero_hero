import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Task 11 - Test the rules (starter).
 *
 * The library classes are the task 10 model; Library has stubs for the
 * reservation methods. The fixture is ready and every test is named: replace
 * each fail("not implemented") with arrange, act and assert. Then add the
 * reservations test first: one test, watch it fail, just enough code, repeat.
 *
 * Run from the repo root (the console launcher prints every test name):
 *   mvn -q compile exec:java@tests -Dtask=11_testing/starter
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
        fail("not implemented");
    }

    @Test
    void returnRestoresAvailable() {
        fail("not implemented");
    }

    @Test
    void borrowUnknownBookRaises() {
        fail("not implemented");
    }

    @Test
    void borrowUnknownMemberRaises() {
        fail("not implemented");
    }

    @Test
    void borrowWithNoCopiesRaises() {
        fail("not implemented");
    }

    @Test
    void returnBookNotOnLoanRaises() {
        fail("not implemented");
    }

    @Test
    void standardLimitIsThreeLoans() {
        fail("not implemented");
    }

    @Test
    void studentLimitIsFiveLoans() {
        fail("not implemented");
    }

    // ---- Dates and fines: build each loan directly, for example new Loan(1, 1, AMIRA, date, null) ----

    @Test
    void dueDateStandardIs14Days() {
        fail("not implemented");
    }

    @Test
    void dueDateStudentIs21Days() {
        fail("not implemented");
    }

    @Test
    void dueDateStaffIs28Days() {
        fail("not implemented");
    }

    @Test
    void dueDateCrossesMonthAndYearEnd() {
        fail("not implemented");
    }

    @Test
    void dueDateInLeapYear() {
        fail("not implemented");
    }

    @Test
    void notOverdueOnDueDate() {
        fail("not implemented");
    }

    @Test
    void fineOneDayLateStandard() {
        fail("not implemented");
    }

    @Test
    void fineTenDaysLateStudent() {
        fail("not implemented");
    }

    @Test
    void staffNeverFined() {
        fail("not implemented");
    }

    @Test
    void lateReturnFineUsesReturnDate() {
        fail("not implemented");
    }

    @Test
    void formatMoney() {
        fail("not implemented");
    }

    // ---- Reservations: in each one, 101 has first borrowed book 5 (the only copy) ----

    @Test
    void reserveBookWithNoFreeCopiesJoinsQueue() {
        fail("not implemented");
    }

    @Test
    void reserveBookWithFreeCopiesRaises() {
        fail("not implemented");
    }

    @Test
    void reserveSameBookTwiceRaises() {
        fail("not implemented");
    }

    @Test
    void returnedCopyIsHeldForFirstInQueue() {
        fail("not implemented");
    }

    @Test
    void firstInQueueCanBorrowHeldCopy() {
        fail("not implemented");
    }

    @Test
    void queueIsFirstComeFirstServed() {
        fail("not implemented");
    }
}
