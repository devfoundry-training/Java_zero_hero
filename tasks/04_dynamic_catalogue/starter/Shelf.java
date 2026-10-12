import java.util.Locale;

/**
 * Task 03 - Shelf: every task 01 and 02 feature as a static method.
 *
 * Stretch 1: the methods live in their own class and Main calls them.
 * Nothing here prints. Every method returns a value (a String, a number,
 * or several lines in one String ending in "\n"), and Main decides what to print.
 */
public class Shelf {

    static final int SIZE = 10;
    static final int OLD_BEFORE = 1950;

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

    /** Stretch 2: two results from one method, carried in a small record. */
    record OldestAndNewest(int oldest, int newest) {
    }

    // ---- Small helpers (task 01) ----

    static String eraOf(int year) {
        return switch (year / 100) {
            case 17 -> "18th century";
            case 18 -> "19th century";
            case 19 -> "20th century";
            case 20 -> "21st century";
            default -> "unknown era";
        };
    }

    static String copiesText(int count) {
        if (count == 0) {
            return "out of stock";
        }
        if (count == 1) {
            return "1 copy";
        }
        return count + " copies";
    }

    static boolean isOld(int year) {
        return year < OLD_BEFORE;
    }

    /** "1 match" or "4 matches": the number and the right noun. */
    static String plural(int count, String singular, String many) {
        return count + " " + (count == 1 ? singular : many);
    }

    // ---- Features (tasks 01 and 02) ----

    static String catalogueLine(int index) {
        String line = IDS[index] + " | " + TITLES[index] + " | " + YEARS[index] + " | "
                + copiesText(COPIES[index]) + " | " + eraOf(YEARS[index]);
        if (isOld(YEARS[index])) {
            line += " | pre-1950";
        }
        return line;
    }

    static String catalogue() {
        StringBuilder text = new StringBuilder("Catalogue (" + SIZE + " books)\n");
        for (int i = 0; i < SIZE; i++) {
            text.append(catalogueLine(i)).append('\n');
        }
        return text.toString();
    }

    /** How many titles contain the term, ignoring case. */
    static int countMatches(String term) {
        int count = 0;
        for (String title : TITLES) {
            if (matches(title, term)) {
                count++;
            }
        }
        return count;
    }

    static boolean matches(String title, String term) {
        return title.toLowerCase(Locale.ROOT).contains(term.toLowerCase(Locale.ROOT));
    }

    /** The search result lines, titles in shelf order. */
    static String search(String term) {
        int count = countMatches(term);
        if (count == 0) {
            return "Search \"" + term + "\": no matches\n";
        }
        StringBuilder text = new StringBuilder("Search \"" + term + "\": " + plural(count, "match", "matches") + "\n");
        for (String title : TITLES) {
            if (matches(title, term)) {
                text.append("  ").append(title).append('\n');
            }
        }
        return text.toString();
    }

    static String report() {
        StringBuilder text = new StringBuilder("Report\n");
        text.append(String.format("%2s  %-30s  %-4s  %6s%n", "ID", "Title", "Year", "Copies"));
        text.append("-".repeat(2)).append("  ").append("-".repeat(30)).append("  ")
                .append("-".repeat(4)).append("  ").append("-".repeat(6)).append('\n');
        for (int i = 0; i < SIZE; i++) {
            text.append(String.format("%2d  %-30s  %-4d  %6d%n", IDS[i], TITLES[i], YEARS[i], COPIES[i]));
        }
        return text.toString();
    }

    static int totalCopies() {
        int total = 0;
        for (int count : COPIES) {
            total += count;
        }
        return total;
    }

    static int countOutOfStock() {
        int count = 0;
        for (int copies : COPIES) {
            if (copies == 0) {
                count++;
            }
        }
        return count;
    }

    static int countOld() {
        int count = 0;
        for (int year : YEARS) {
            if (isOld(year)) {
                count++;
            }
        }
        return count;
    }

    static int averageYear() {
        int total = 0;
        for (int year : YEARS) {
            total += year;
        }
        return total / SIZE;    // integer division
    }

    static int wordCount() {
        int words = 0;
        for (String title : TITLES) {
            words += title.trim().split("\\s+").length;
        }
        return words;
    }

    static String longestTitle() {
        String longest = TITLES[0];
        for (int i = 1; i < SIZE; i++) {
            if (TITLES[i].length() > longest.length()) {
                longest = TITLES[i];
            }
        }
        return longest;
    }

    /** Stretch 2: the index of the oldest and of the newest book, in one record. */
    static OldestAndNewest oldestAndNewest() {
        int oldest = 0;
        int newest = 0;
        for (int i = 1; i < SIZE; i++) {
            if (YEARS[i] < YEARS[oldest]) {
                oldest = i;
            }
            if (YEARS[i] > YEARS[newest]) {
                newest = i;
            }
        }
        return new OldestAndNewest(oldest, newest);
    }

    static String stats(boolean includeStretch) {
        String longest = longestTitle();
        StringBuilder text = new StringBuilder("Stats\n");
        text.append("books: ").append(SIZE).append('\n');
        text.append("copies: ").append(totalCopies()).append('\n');
        text.append("out of stock: ").append(countOutOfStock()).append('\n');
        text.append("pre-1950: ").append(countOld()).append('\n');
        text.append("average year: ").append(averageYear()).append('\n');
        text.append("words in titles: ").append(wordCount()).append('\n');
        text.append("longest title: ").append(longest).append(" (").append(longest.length()).append(" characters)\n");
        if (includeStretch) {
            OldestAndNewest result = oldestAndNewest();
            text.append("oldest: ").append(TITLES[result.oldest()]).append(" (").append(YEARS[result.oldest()]).append(")\n");
            text.append("newest: ").append(TITLES[result.newest()]).append(" (").append(YEARS[result.newest()]).append(")\n");
        }
        return text.toString();
    }
}
