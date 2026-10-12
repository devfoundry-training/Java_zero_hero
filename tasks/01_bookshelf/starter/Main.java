/**
 * Task 01 - Bookshelf (starter).
 *
 * The ten books are already in four parallel arrays. Fill in each method marked
 * TODO, then run the check (see tasks/README.md). README.md in the task folder
 * has the full brief and the exact output format.
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=01_bookshelf/starter
 */
public class Main {

    // ---- Core 1: the shelf, four parallel arrays of length SIZE ----
    // Position i in every array describes the same book.

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

    public static void main(String[] args) {
        printCatalogue();
        printOddIds();
        printTotals();
    }

    /** TODO Core 4: a switch expression on year / 100 (17 is "18th century", ... anything else "unknown era"). */
    static String eraOf(int year) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** TODO Core 3: "out of stock", "1 copy" or "N copies", with if / else. */
    static String copiesText(int count) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * TODO Core 2 and 5: print "Catalogue (10 books)", then one line per book:
     * id | title | year | copies text | century, plus " | pre-1950" for books before 1950.
     */
    static void printCatalogue() {
        System.out.println("Core 2: catalogue not implemented");
    }

    /**
     * TODO Core 6: copy the odd-id books into a second pair of arrays of the same
     * size, count the slots used, then print "Odd ids only (N books)" and "id | title" lines.
     */
    static void printOddIds() {
        System.out.println("Core 6: odd ids not implemented");
    }

    /** TODO Core 7: print "Totals" and the five total lines (average year with integer division). */
    static void printTotals() {
        System.out.println("Core 7: totals not implemented");
    }
}
