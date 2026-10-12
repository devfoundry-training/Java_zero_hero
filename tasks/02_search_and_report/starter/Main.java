/**
 * Task 02 - Search and report (starter).
 *
 * The same ten books as task 01. Bookshelf.java next to this file is the task 01
 * solution, if you want to look back at it. Fill in each method marked TODO,
 * then run the check (see tasks/README.md).
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=02_search_and_report/starter
 */
public class Main {

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

    static final String[] SEARCH_TERMS = {"the", "TIME", "zzz"};

    public static void main(String[] args) {
        printReport();
        for (String term : SEARCH_TERMS) {
            printSearch(term);
        }
        printWordCount();
        printLongestTitle();
    }

    /** TODO Core 1: "Report", the header row, the row of dashes and one row per book (String.format). */
    static void printReport() {
        System.out.println("Core 1: report not implemented");
    }

    /** TODO Core 2: collect the matching titles with a while loop into a fixed array plus a count. */
    static void printSearch(String term) {
        System.out.println("Core 2: search for \"" + term + "\" not implemented");
    }

    /** TODO Core 3: "Words in titles: N". */
    static void printWordCount() {
        System.out.println("Core 3: word count not implemented");
    }

    /** TODO Core 4: "Longest title: TITLE (N characters)"; the first one wins a tie. */
    static void printLongestTitle() {
        System.out.println("Core 4: longest title not implemented");
    }
}
