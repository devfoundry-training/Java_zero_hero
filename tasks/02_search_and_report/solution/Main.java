import java.util.Locale;

/**
 * Task 02 - Search and report (solution, Core + Stretch).
 *
 * Session 2: loops and strings. Builds on task 01 (the same shelf).
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=02_search_and_report/solution
 *   mvn -q compile exec:java@task -Dtask=02_search_and_report/solution -Dexec.args="--stretch"
 */
public class Main {

    // ---- The shelf from task 01 (parallel arrays, never resized) ----

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

        if (args.length > 0 && args[0].equals("--stretch")) {
            printStretch();
        }
    }

    // ---- Core 1: a formatted report table ----
    // Column widths: id 2 (right), title 30 (left), year 4, copies 6 (right),
    // two spaces between columns. %-30s pads on the right (left-aligns),
    // %6d pads on the left (right-aligns).

    static void printReport() {
        System.out.println("Report");
        System.out.println(String.format("%2s  %-30s  %-4s  %6s", "ID", "Title", "Year", "Copies"));
        System.out.println("-".repeat(2) + "  " + "-".repeat(30) + "  " + "-".repeat(4) + "  " + "-".repeat(6));
        for (int i = 0; i < SIZE; i++) {
            System.out.println(String.format("%2d  %-30s  %-4d  %6d", IDS[i], TITLES[i], YEARS[i], COPIES[i]));
        }
    }

    // ---- Core 2: case-insensitive search ----
    // Lower-case both sides, then look for the term anywhere in the title.
    // Locale.ROOT keeps the rules the same on every machine (in Turkish,
    // "TIME".toLowerCase() would give a dotless i). A while loop collects the
    // matches into a fixed array plus a count.

    static void printSearch(String term) {
        String wanted = term.toLowerCase(Locale.ROOT);
        String[] found = new String[SIZE];
        int count = 0;
        int i = 0;
        while (i < SIZE) {
            if (TITLES[i].toLowerCase(Locale.ROOT).contains(wanted)) {
                found[count] = TITLES[i];
                count++;
            }
            i++;
        }

        if (count == 0) {
            System.out.println("Search \"" + term + "\": no matches");
            return;
        }
        String noun = count == 1 ? "match" : "matches";
        System.out.println("Search \"" + term + "\": " + count + " " + noun);
        for (int j = 0; j < count; j++) {
            System.out.println("  " + found[j]);
        }
    }

    // ---- Core 3: count the words in all titles ----
    // split("\\s+") splits on any run of whitespace, so "Eighty-Four" stays one word.

    static void printWordCount() {
        int words = 0;
        for (String title : TITLES) {
            words += title.trim().split("\\s+").length;
        }
        System.out.println("Words in titles: " + words);
    }

    // ---- Core 4: the longest title (the first one wins a tie) ----

    static void printLongestTitle() {
        int longest = 0;
        for (int i = 1; i < SIZE; i++) {
            if (TITLES[i].length() > TITLES[longest].length()) {   // > not >=, so a tie keeps the first
                longest = i;
            }
        }
        System.out.println("Longest title: " + TITLES[longest] + " (" + TITLES[longest].length() + " characters)");
    }

    // ---- Stretch: initials, the shortest title, a compact list ----

    static void printStretch() {
        System.out.println("Stretch");
        for (String title : TITLES) {
            StringBuilder initials = new StringBuilder();
            for (String word : title.trim().split("\\s+")) {
                initials.append(Character.toUpperCase(word.charAt(0)));
            }
            System.out.println(String.format("%-6s%s", initials, title));
        }

        int shortest = 0;
        for (int i = 1; i < SIZE; i++) {
            if (TITLES[i].length() < TITLES[shortest].length()) {
                shortest = i;
            }
        }
        System.out.println("Shortest title: " + TITLES[shortest] + " (" + TITLES[shortest].length() + " characters)");

        // Titles longer than 20 characters are cut to 17 plus "..."
        for (int i = 0; i < SIZE; i++) {
            String title = TITLES[i];
            String shortTitle = title.length() <= 20 ? title : title.substring(0, 17) + "...";
            System.out.println(String.format("%2d  %-20s  %d", IDS[i], shortTitle, YEARS[i]));
        }
    }
}
