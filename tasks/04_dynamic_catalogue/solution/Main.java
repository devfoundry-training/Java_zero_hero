import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Task 04 - Dynamic catalogue (solution, Core + Stretch).
 *
 * Session 4: collections. Builds on task 03: the fixed shelf of 10 becomes
 * a List of all 20 books, plus a Map and a Set.
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=04_dynamic_catalogue/solution
 *   mvn -q compile exec:java@task -Dtask=04_dynamic_catalogue/solution -Dexec.args="--stretch"
 */
public class Main {

    // ---- The data: the lines of data/books.csv, copied in ----
    // Reading files comes in task 08. Until then the program carries its own
    // copy of the CSV lines, header first.

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

    /** One step of the borrowing script. */
    record Action(String kind, int bookId) {
    }

    static final List<Action> ACTIONS = List.of(
            new Action("borrow", 1),
            new Action("borrow", 1),
            new Action("borrow", 4),
            new Action("return", 1),
            new Action("borrow", 3),
            new Action("borrow", 7),
            new Action("return", 9),
            new Action("borrow", 1));

    /** One row of books.csv. A record is a class with no behaviour (classes come in task 05). */
    record Book(int id, String title, String author, int year, String genre, int copies, String format, int length) {
    }

    /** A-Z ignoring case: compare the lower-case text, character by character. */
    static final Comparator<String> BY_NAME = Comparator.comparing((String text) -> text.toLowerCase(Locale.ROOT));

    static Book parseBook(String line) {
        String[] f = line.split(",");
        return new Book(Integer.parseInt(f[0]), f[1], f[2], Integer.parseInt(f[3]), f[4],
                Integer.parseInt(f[5]), f[6], Integer.parseInt(f[7]));
    }

    public static void main(String[] args) {

        // ---- Core 1: load every line into a list ----
        List<Book> books = new ArrayList<>();
        for (String line : BOOK_LINES.subList(1, BOOK_LINES.size())) {    // subList(1, ...) skips the header
            books.add(parseBook(line));
        }
        System.out.println("Loaded " + books.size() + " books");

        // ---- Core 2: index by genre (a TreeMap keeps its keys sorted) ----
        Map<String, List<String>> byGenre = new TreeMap<>(BY_NAME);
        for (Book book : books) {
            byGenre.computeIfAbsent(book.genre(), genre -> new ArrayList<>()).add(book.title());
        }
        System.out.println("Genres (" + byGenre.size() + ")");
        for (Map.Entry<String, List<String>> entry : byGenre.entrySet()) {
            List<String> titles = entry.getValue();
            System.out.println(entry.getKey() + " (" + titles.size() + "): " + String.join(", ", titles));
        }

        // ---- Core 3: unique authors (a Set drops the duplicates for us) ----
        Set<String> authors = new HashSet<>();
        for (Book book : books) {
            authors.add(book.author());
        }
        List<String> sortedAuthors = new ArrayList<>(authors);
        sortedAuthors.sort(BY_NAME);
        System.out.println("Authors (" + authors.size() + " unique across " + books.size() + " books)");
        for (String author : sortedAuthors) {
            System.out.println(author);
        }

        // ---- Core 4 and 5: borrow and return, counted in maps keyed by book id ----
        Map<Integer, Book> catalogue = new HashMap<>();
        for (Book book : books) {
            catalogue.put(book.id(), book);
        }
        Map<Integer, Integer> onLoan = new HashMap<>();
        Map<Integer, Integer> borrowedCount = new TreeMap<>();     // sorted by id for the report
        Map<Integer, Integer> returnedCount = new HashMap<>();
        int borrows = 0;
        int returns = 0;
        int refused = 0;

        System.out.println("Borrowing");
        for (Action action : ACTIONS) {
            int id = action.bookId();
            Book book = catalogue.get(id);
            int out = onLoan.getOrDefault(id, 0);
            if (action.kind().equals("borrow")) {
                if (out < book.copies()) {
                    onLoan.put(id, out + 1);
                    borrowedCount.merge(id, 1, Integer::sum);
                    borrows++;
                    System.out.println("borrow " + id + ": OK, " + (book.copies() - out - 1) + " left");
                } else {
                    refused++;
                    System.out.println("borrow " + id + ": refused, none available");
                }
            } else {
                if (out > 0) {
                    onLoan.put(id, out - 1);
                    returnedCount.merge(id, 1, Integer::sum);
                    returns++;
                    System.out.println("return " + id + ": OK, " + (book.copies() - out + 1) + " left");
                } else {
                    refused++;
                    System.out.println("return " + id + ": refused, none on loan");
                }
            }
        }

        System.out.println("Counts");
        System.out.println("borrows: " + borrows);
        System.out.println("returns: " + returns);
        System.out.println("refused: " + refused);
        for (Map.Entry<Integer, Integer> entry : borrowedCount.entrySet()) {
            int id = entry.getKey();
            System.out.println("book " + id + ": borrowed " + entry.getValue()
                    + ", returned " + returnedCount.getOrDefault(id, 0));
        }

        if (args.length > 0 && args[0].equals("--stretch")) {
            printStretch(books, byGenre);
        }
    }

    // ---- Stretch: busy authors, books per decade, the biggest genre ----

    static void printStretch(List<Book> books, Map<String, List<String>> byGenre) {
        System.out.println("Stretch");

        Map<String, Integer> perAuthor = new TreeMap<>(BY_NAME);
        for (Book book : books) {
            perAuthor.merge(book.author(), 1, Integer::sum);
        }
        List<String> busy = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : perAuthor.entrySet()) {
            if (entry.getValue() > 1) {
                busy.add(entry.getKey());
            }
        }
        System.out.println("Authors with more than one book: " + String.join(", ", busy));

        Map<Integer, Integer> perDecade = new TreeMap<>();
        for (Book book : books) {
            int decade = book.year() / 10 * 10;    // 1813 -> 1810
            perDecade.merge(decade, 1, Integer::sum);
        }
        for (Map.Entry<Integer, Integer> entry : perDecade.entrySet()) {
            System.out.println(entry.getKey() + "s: " + entry.getValue());
        }

        // Most books wins; byGenre is already A-Z, so a tie keeps the first
        String biggest = null;
        for (String genre : byGenre.keySet()) {
            if (biggest == null || byGenre.get(genre).size() > byGenre.get(biggest).size()) {
                biggest = genre;
            }
        }
        System.out.println("Biggest genre: " + biggest + " (" + byGenre.get(biggest).size() + " books)");
    }
}
