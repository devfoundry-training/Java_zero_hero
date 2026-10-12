import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Task 05 - Classes (solution, Core + Stretch).
 *
 * Session 5: classes and encapsulation. Builds on task 04: the records and
 * maps become Book, Member and Library objects with methods.
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=05_classes/solution
 *   mvn -q compile exec:java@task -Dtask=05_classes/solution -Dexec.args="--stretch"
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

    /** One line of the borrowing script. */
    record Step(String action, int memberId, int bookId) {
    }

    static Library loadLibrary() {
        Library library = new Library();
        for (String line : BOOK_LINES.subList(1, BOOK_LINES.size())) {
            String[] f = line.split(",");
            library.addBook(new Book(Integer.parseInt(f[0]), f[1], f[2], Integer.parseInt(f[3]), f[4],
                    Integer.parseInt(f[5])));
        }
        for (String line : MEMBER_LINES.subList(1, MEMBER_LINES.size())) {
            String[] f = line.split(",");
            library.addMember(new Member(Integer.parseInt(f[0]), f[1], f[2], f[3], f[4]));
        }
        return library;
    }

    static String plural(int count, String singular, String many) {
        return count + " " + (count == 1 ? singular : many);
    }

    public static void main(String[] args) {
        Library library = loadLibrary();
        System.out.println("Library: " + library.bookCount() + " books, " + library.memberCount() + " members");
        // orElseThrow: these ids are in the data, so a missing one is a bug worth stopping for
        System.out.println(library.findById(4).orElseThrow());
        System.out.println(library.findMember(101).orElseThrow());

        for (String text : List.of("the", "zzz")) {
            List<Book> found = library.findByTitle(text);
            if (found.isEmpty()) {
                System.out.println("Find \"" + text + "\": no books");
                continue;
            }
            System.out.println("Find \"" + text + "\": " + plural(found.size(), "book", "books"));
            for (Book book : found) {
                System.out.println("  " + book);
            }
        }

        // Not found is an empty Optional, never null
        Optional<Book> missing = library.findById(99);
        System.out.println(missing.isEmpty() ? "Find id 99: not found" : "Find id 99: " + missing.get());

        System.out.println("Borrowing");
        List<Step> script = List.of(
                new Step("borrow", 101, 4),
                new Step("borrow", 101, 3),
                new Step("borrow", 999, 1),
                new Step("borrow", 101, 99),
                new Step("borrow", 102, 5),
                new Step("borrow", 103, 5),
                new Step("return", 101, 4),
                new Step("return", 101, 4));
        for (Step step : script) {
            boolean ok = step.action().equals("borrow")
                    ? library.borrow(step.memberId(), step.bookId())
                    : library.returnBook(step.memberId(), step.bookId());
            System.out.println(step.action() + " " + step.memberId() + " " + step.bookId() + ": "
                    + (ok ? "OK" : "refused"));
        }

        System.out.println("After borrowing");
        System.out.println(library.findById(4).orElseThrow());
        System.out.println(library.findById(5).orElseThrow());
        System.out.println(library.findMember(101).orElseThrow());
        System.out.println(library.findMember(102).orElseThrow());

        System.out.println("Validation");
        List<Runnable> attempts = List.of(
                () -> new Book(0, "Dune", "Frank Herbert", 1965, "Science Fiction", 4),
                () -> new Book(21, "   ", "Frank Herbert", 1965, "Science Fiction", 4),
                () -> new Book(21, "Dune", "Frank Herbert", 3000, "Science Fiction", 4),
                () -> new Book(21, "Dune", "Frank Herbert", 1965, "Science Fiction", -1),
                () -> new Member(110, "Sam Green", "sam.green@example.com", "visitor", "2026-10-12"));
        for (Runnable attempt : attempts) {
            try {
                attempt.run();
                System.out.println("Accepted");
            } catch (IllegalArgumentException error) {
                System.out.println("Rejected: " + error.getMessage());
            }
        }

        if (args.length > 0 && args[0].equals("--stretch")) {
            printStretch(library);
        }
    }

    static void printStretch(Library library) {
        System.out.println("Stretch");
        Book copy = new Book(4, "Dune (second copy record)", "Frank Herbert", 1965, "Science Fiction", 1);
        System.out.println("same book as #4: " + copy.equals(library.findById(4).orElseThrow()));

        List<Book> sorted = new ArrayList<>(library.allBooks());
        sorted.sort(null);      // null means "use the natural order": Book.compareTo
        List<String> firstFive = new ArrayList<>();
        for (Book book : sorted.subList(0, 5)) {
            firstFive.add(book.getTitle() + " (" + book.getYear() + ")");
        }
        System.out.println("first 5 by title: " + String.join(", ", firstFive));
        System.out.println("available books: " + library.availableBooks().size());
    }
}
