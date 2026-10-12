import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Task 03 - Functions and a menu (solution, Core + Stretch).
 *
 * Session 3: methods. Builds on task 02. The features live in Shelf.java
 * (Stretch 1); this class only runs the menu and prints.
 *
 * Run from the repo root (the menu reads its choices from standard input):
 *   mvn -q compile exec:java@task -Dtask=03_functions/solution < tasks/data/input_03.txt
 *   mvn -q compile exec:java@task -Dtask=03_functions/solution -Dexec.args="--stretch" < tasks/data/input_03.txt
 * Type the choices yourself if you run it without "<".
 */
public class Main {

    static final String MENU = "Menu: 1 catalogue | 2 search | 3 report | 4 stats | 0 quit";

    static final BufferedReader INPUT = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public static void main(String[] args) throws IOException {
        boolean includeStretch = args.length > 0 && args[0].equals("--stretch");
        runMenu(includeStretch);
    }

    /**
     * Reads one line from the keyboard (or a piped file).
     * Returns the line without surrounding spaces, or null at the end of the input.
     * The line is echoed as "> text" so the output reads like a transcript.
     */
    static String readLine() throws IOException {
        String line = INPUT.readLine();
        if (line == null) {         // readLine returns null only at the end of the input
            return null;
        }
        String text = line.trim();
        System.out.println(text.isEmpty() ? ">" : "> " + text);    // never a trailing space
        return text;
    }

    static void runMenu(boolean includeStretch) throws IOException {
        System.out.println("Library Manager");
        while (true) {
            System.out.println(MENU);
            String choice = readLine();
            if (choice == null || choice.equals("0")) {
                break;
            }

            // The choice is compared as text, so "abc" is just another
            // unknown choice: no number parsing, nothing to go wrong.
            switch (choice) {
                case "1" -> System.out.print(Shelf.catalogue());
                case "2" -> {
                    System.out.println("Search for:");
                    String term = readLine();
                    if (term == null) {
                        System.out.println("Goodbye");
                        return;
                    }
                    System.out.print(Shelf.search(term));
                }
                case "3" -> System.out.print(Shelf.report());
                case "4" -> System.out.print(Shelf.stats(includeStretch));
                default -> System.out.println("Unknown choice: " + choice);
            }
        }
        System.out.println("Goodbye");
    }
}
