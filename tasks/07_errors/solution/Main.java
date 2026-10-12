import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Task 07 - Errors done properly (solution, Core + Stretch).
 *
 * Session 7: exceptions. Builds on task 06.
 * Reads commands, one per line, and never crashes:
 *   borrow MEMBER_ID BOOK_ID | return MEMBER_ID BOOK_ID
 *   loans MEMBER_ID          | quit
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=07_errors/solution < tasks/data/input_07.txt
 *   mvn -q compile exec:java@task -Dtask=07_errors/solution -Dexec.args="--stretch --debug" < tasks/data/input_07.txt
 */
public class Main {

    static final List<String> BOOK_LINES = List.of(
            "id,title,author,year,genre,copies,format,length",
            "1,Pride and Prejudice,Jane Austen,1813,Classic,3,print,432",
            "2,Nineteen Eighty-Four,George Orwell,1949,Dystopian,2,print,328",
            "3,The Hobbit,J. R. R. Tolkien,1937,Fantasy,0,print,310",
            "4,Dune,Frank Herbert,1965,Science Fiction,4,print,412",
            "5,Robinson Crusoe,Daniel Defoe,1719,Adventure,1,print,320",
            "6,The Time Machine,H. G. Wells,1895,Science Fiction,2,ebook,118",
            "7,Animal Farm,George Orwell,1945,Dystopian,5,print,112",
            "8,Emma,Jane Austen,1815,Classic,1,audio,1150",
            "9,The Left Hand of Darkness,Ursula K. Le Guin,1969,Science Fiction,0,print,304",
            "10,Murder on the Orient Express,Agatha Christie,1934,Crime,2,print,256",
            "11,The Murder of Roger Ackroyd,Agatha Christie,1926,Crime,1,ebook,312",
            "12,A Wizard of Earthsea,Ursula K. Le Guin,1968,Fantasy,2,print,183",
            "13,Brave New World,Aldous Huxley,1932,Dystopian,2,audio,480",
            "14,The Martian,Andy Weir,2011,Science Fiction,3,print,369",
            "15,Small Island,Andrea Levy,2004,Historical,1,print,441",
            "16,Wolf Hall,Hilary Mantel,2009,Historical,2,audio,1440",
            "17,The Remains of the Day,Kazuo Ishiguro,1989,Literary,2,print,258",
            "18,Never Let Me Go,Kazuo Ishiguro,2005,Literary,1,ebook,288",
            "19,Frankenstein,Mary Shelley,1818,Gothic,2,print,280",
            "20,The War of the Worlds,H. G. Wells,1898,Science Fiction,3,audio,410");

    static final List<String> MEMBER_LINES = List.of(
            "id,name,email,type,joined",
            "101,Amira Khan,amira.khan@example.com,standard,2023-01-15",
            "102,Ben Taylor,ben.taylor@example.com,student,2024-09-02",
            "103,Chloe Evans,chloe.evans@example.com,staff,2019-05-20",
            "104,Dev Patel,dev.patel@example.com,student,2025-09-10",
            "105,Ella Brown,ella.brown@example.com,standard,2021-03-08",
            "106,Femi Adeyemi,femi.adeyemi@example.com,standard,2022-11-30",
            "107,Grace Wilson,grace.wilson@example.com,staff,2018-07-01",
            "108,Harry Jones,harry.jones@example.com,student,2026-01-12",
            "109,Ben Taylor,ben.taylor2@example.com,standard,2025-02-14");

    static Library loadLibrary() {
        Library library = new Library();
        for (String line : BOOK_LINES.subList(1, BOOK_LINES.size())) {
            library.addBook(Book.makeBook(line.split(",")));
        }
        for (String line : MEMBER_LINES.subList(1, MEMBER_LINES.size())) {
            String[] f = line.split(",");
            library.addMember(new Member(Integer.parseInt(f[0]), f[1], f[2], f[3], f[4]));
        }
        return library;
    }

    // ---- Turning text into commands ----

    /**
     * "4" becomes 4. Anything else becomes a CommandException we wrote.
     * A whole number is digits with an optional minus sign. Integer.parseInt on
     * its own would also accept "+4", so the pattern is checked first.
     */
    static int parseId(String text, String what) {
        try {
            if (!text.matches("-?[0-9]+")) {
                throw new NumberFormatException("For input string: \"" + text + "\"");
            }
            return Integer.parseInt(text);      // can still fail: a number too big for an int
        } catch (NumberFormatException error) {
            // Chaining: the NumberFormatException travels inside ours as the cause
            throw new CommandException(what + " must be a whole number, got '" + text + "'", error);
        }
    }

    /**
     * Does one command and returns the line to print.
     * Throws LibraryException (or a subclass) when the command cannot be done.
     */
    static String runCommand(Library library, String[] words) {
        String command = words[0].toLowerCase(Locale.ROOT);
        int argCount = words.length - 1;

        switch (command) {
            case "borrow", "return" -> {
                if (argCount != 2) {
                    throw new CommandException("usage: " + command + " MEMBER_ID BOOK_ID");
                }
                int memberId = parseId(words[1], "member id");
                int bookId = parseId(words[2], "book id");
                if (command.equals("borrow")) {
                    library.borrow(memberId, bookId);
                    Member member = library.member(memberId);
                    return "OK: " + member.getName() + " borrowed " + library.book(bookId).getTitle()
                            + ", due in " + member.getPolicy().loanDays() + " days";
                }
                library.returnBook(memberId, bookId);
                return "OK: " + library.member(memberId).getName() + " returned " + library.book(bookId).getTitle();
            }
            case "loans" -> {
                if (argCount != 1) {
                    throw new CommandException("usage: loans MEMBER_ID");
                }
                Member member = library.member(parseId(words[1], "member id"));
                List<Integer> loans = member.getLoans();
                if (loans.isEmpty()) {
                    return member.getName() + " has no loans";
                }
                List<String> titles = new ArrayList<>();
                for (int bookId : loans) {
                    titles.add(library.book(bookId).getTitle());
                }
                return member.getName() + " has " + Text.plural(titles.size(), "loan", "loans") + ": "
                        + String.join(", ", titles);
            }
            default -> throw new CommandException("unknown command '" + words[0] + "'");
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> flags = List.of(args);
        Library library = loadLibrary();
        System.out.println("Library Manager: " + library.bookCount() + " books, " + library.memberCount() + " members");

        int commands = 0;
        int ok = 0;
        int errors = 0;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        try {
            String line;
            while ((line = input.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {        // blank lines are skipped without a word
                    continue;
                }
                String[] words = trimmed.split("\\s+");
                System.out.println("> " + String.join(" ", words));
                if (words[0].equalsIgnoreCase("quit")) {
                    break;
                }
                commands++;
                try {
                    System.out.println(runCommand(library, words));
                    ok++;
                } catch (LibraryException error) {
                    // The specific type first: our own errors, with our own messages
                    errors++;
                    System.out.println("Error: " + error.getMessage());
                    if (flags.contains("--debug") && error.getCause() != null) {
                        System.out.println("  cause: " + error.getCause());   // Stretch 2: the chained exception
                    }
                } catch (RuntimeException error) {
                    // Then the general: a bug of ours. Report it and keep going.
                    errors++;
                    System.out.println("Error: unexpected problem");
                }
            }
        } finally {
            // finally runs however the loop ends: quit, the end of the input, or an exception
            if (flags.contains("--stretch")) {
                System.out.println("Session: " + commands + " commands, " + ok + " OK, " + errors + " errors");
            }
            System.out.println("Goodbye");
        }
    }
}
