import java.util.ArrayList;
import java.util.List;

/**
 * What a load produced: the good rows and the problems.
 * A bad line is recorded as "line N: reason" and skipped; the good lines still load.
 */
public class LoadResult {

    /** A loan saved in library.json: who has which book. */
    public record SavedLoan(int memberId, int bookId) {
    }

    final List<Book> books = new ArrayList<>();
    final List<Member> members = new ArrayList<>();
    final List<SavedLoan> loans = new ArrayList<>();
    final List<String> problems = new ArrayList<>();
    int skipped;

    void skip(int lineNumber, String reason) {
        problems.add("line " + lineNumber + ": " + reason);
        skipped++;
    }
}
