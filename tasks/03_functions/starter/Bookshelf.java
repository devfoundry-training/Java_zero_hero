/**
 * Task 01 - Bookshelf (solution, Core + Stretch).
 *
 * Kept here for task 03: turn its features into methods (it was Main in task 01).
 *
 * Session 1: data types, operators, conditionals, switch, arrays.
 */
public class Bookshelf {

    // ---- The shelf: four parallel arrays, one per field ----
    // A Java array has a fixed size from the moment it is created.
    // Position i in every array describes the same book.

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

    public static void main(String[] args) {
        printCatalogue();
        printOddIds();
        printTotals();

        if (args.length > 0 && args[0].equals("--stretch")) {
            printStretch();
        }
    }

    /**
     * Names the century with a switch expression on year / 100.
     * Both values are int, so / is integer division: 1813 / 100 is 18, never 18.13.
     */
    static String eraOf(int year) {
        return switch (year / 100) {
            case 17 -> "18th century";
            case 18 -> "19th century";
            case 19 -> "20th century";
            case 20 -> "21st century";
            default -> "unknown era";
        };
    }

    /** "out of stock", "1 copy" or "N copies", chosen with if / else. */
    static String copiesText(int count) {
        if (count == 0) {
            return "out of stock";
        } else if (count == 1) {
            return "1 copy";
        } else {
            return count + " copies";
        }
    }

    // ---- Core 1 to 3: the catalogue with copies, era and the pre-1950 flag ----

    static void printCatalogue() {
        System.out.println("Catalogue (" + SIZE + " books)");
        for (int i = 0; i < SIZE; i++) {
            String line = IDS[i] + " | " + TITLES[i] + " | " + YEARS[i] + " | "
                    + copiesText(COPIES[i]) + " | " + eraOf(YEARS[i]);
            if (YEARS[i] < OLD_BEFORE) {
                line += " | pre-1950";
            }
            System.out.println(line);
        }
    }

    // ---- Core 4: "remove" the even ids ----
    // An array cannot shrink, so the books we keep are copied into a
    // second pair of arrays of the same size, with a count of the slots used.

    static void printOddIds() {
        int[] keptIds = new int[SIZE];
        String[] keptTitles = new String[SIZE];
        int kept = 0;
        for (int i = 0; i < SIZE; i++) {
            if (IDS[i] % 2 != 0) {          // % is the remainder: odd ids leave 1
                keptIds[kept] = IDS[i];
                keptTitles[kept] = TITLES[i];
                kept++;
            }
        }

        System.out.println("Odd ids only (" + kept + " books)");
        for (int i = 0; i < kept; i++) {
            System.out.println(keptIds[i] + " | " + keptTitles[i]);
        }
    }

    // ---- Core 5: totals with arithmetic and comparison operators ----

    static void printTotals() {
        int totalCopies = 0;
        int outOfStock = 0;
        int oldBooks = 0;
        int yearSum = 0;
        for (int i = 0; i < SIZE; i++) {
            totalCopies += COPIES[i];
            yearSum += YEARS[i];
            if (COPIES[i] == 0) {
                outOfStock++;
            }
            if (YEARS[i] < OLD_BEFORE) {
                oldBooks++;
            }
        }

        System.out.println("Totals");
        System.out.println("books: " + SIZE);
        System.out.println("copies: " + totalCopies);
        System.out.println("out of stock: " + outOfStock);
        System.out.println("pre-1950: " + oldBooks);
        System.out.println("average year: " + yearSum / SIZE);  // int / int rounds down
    }

    // ---- Stretch: oldest and newest, and a count per era ----

    static void printStretch() {
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
        System.out.println("Stretch");
        System.out.println("oldest: " + TITLES[oldest] + " (" + YEARS[oldest] + ")");
        System.out.println("newest: " + TITLES[newest] + " (" + YEARS[newest] + ")");

        // One counter per era, in a fixed order
        String[] eraNames = {"18th century", "19th century", "20th century", "21st century"};
        int[] eraCounts = new int[eraNames.length];
        for (int i = 0; i < SIZE; i++) {
            for (int e = 0; e < eraNames.length; e++) {
                if (eraOf(YEARS[i]).equals(eraNames[e])) {   // equals, never == for String
                    eraCounts[e]++;
                }
            }
        }
        for (int e = 0; e < eraNames.length; e++) {
            System.out.println(eraNames[e] + ": " + eraCounts[e]);
        }
    }
}
