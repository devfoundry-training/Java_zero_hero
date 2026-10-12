import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Task 03 - Functions and a menu (starter).
 *
 * Bookshelf.java (task 01) and SearchAndReport.java (task 02) are the solutions
 * so far: each is one long main. Turn their features into the methods below,
 * each taking what it needs and RETURNING its result, then drive them from a
 * menu loop. Only the menu prints.
 *
 * Run from the repo root (the menu reads its choices from standard input):
 *   mvn -q compile exec:java@task -Dtask=03_functions/starter < tasks/data/input_03.txt
 */
public class Main {

    static final String MENU = "Menu: 1 catalogue | 2 search | 3 report | 4 stats | 0 quit";

    static final int SIZE = 10;

    static final int[] IDS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    static final String[] TITLES = {
            "Pride and Prejudice",
            "Nineteen Eighty-Four",
            "The Hobbit",
            "Dune",
            "Robinson Crusoe",
            "The Time Machine",
            "Animal Farm",
            "Emma",
            "The Left Hand of Darkness",
            "Murder on the Orient Express",
    };
    static final int[] YEARS = {1813, 1949, 1937, 1965, 1719, 1895, 1945, 1815, 1969, 1934};
    static final int[] COPIES = {3, 2, 0, 4, 1, 2, 5, 1, 0, 2};

    static final BufferedReader INPUT = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public static void main(String[] args) throws IOException {
        runMenu();
    }

    /**
     * Reads one line, or returns null at the end of the input.
     * TODO Core 4: echo the trimmed line as "> TEXT" (a blank line echoes as ">").
     */
    static String readLine() throws IOException {
        String line = INPUT.readLine();
        return line == null ? null : line.trim();
    }

    /**
     * TODO Core 2 to 5: print "Library Manager", then loop: print MENU, read a choice,
     * compare it as text ("1", "2", "3", "4", "0", anything else) and print the result.
     * When the input ends without a 0, behave as if 0 was typed. Finish with "Goodbye".
     */
    static void runMenu() throws IOException {
        System.out.println("Core: menu not implemented");
    }

    // ---- TODO Core 1: every task 01 and 02 feature as a method that returns its result ----

    static String eraOf(int year) {
        throw new UnsupportedOperationException("not implemented");
    }

    static String copiesText(int count) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** One catalogue line, for the book at this index. */
    static String catalogueLine(int index) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** The search result lines for one term (task 02 format). */
    static String search(String term) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** The report table (task 02 format), starting with "Report". */
    static String report() {
        throw new UnsupportedOperationException("not implemented");
    }

    static int wordCount() {
        throw new UnsupportedOperationException("not implemented");
    }

    static String longestTitle() {
        throw new UnsupportedOperationException("not implemented");
    }

    static int totalCopies() {
        throw new UnsupportedOperationException("not implemented");
    }

    static int averageYear() {
        throw new UnsupportedOperationException("not implemented");
    }
}
