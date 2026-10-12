import java.util.List;

/**
 * Task 04 - Dynamic catalogue (starter).
 *
 * Menu.java and Shelf.java are the task 03 solution. This task grows the shelf
 * of ten into a List of all twenty books, with a Map, a Set and some counters.
 * Fill in each part marked TODO, then run the check (see tasks/README.md).
 *
 * Run from the repo root:
 *   mvn -q compile exec:java@task -Dtask=04_dynamic_catalogue/starter
 */
public class Main {

    // The lines of data/books.csv, header first (reading files comes in task 08)
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

    /** One row of books.csv. */
    record Book(int id, String title, String author, int year, String genre, int copies, String format, int length) {
    }

    /** TODO Core 1: split the line on commas and build a Book (Integer.parseInt for the numbers). */
    static Book parseBook(String line) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static void main(String[] args) {
        // TODO Core 1: load every data line into a List<Book>, print "Loaded N books"
        System.out.println("Core 1: loading not implemented");
        // TODO Core 2: a Map from genre to titles; print the genres A-Z with counts and titles
        System.out.println("Core 2: genres not implemented");
        // TODO Core 3: a Set of authors; print the count, then the authors A-Z
        System.out.println("Core 3: authors not implemented");
        // TODO Core 4: run ACTIONS against a Map of copies on loan, one line per action
        System.out.println("Core 4: borrowing not implemented");
        // TODO Core 5: print the counts, then "book ID: borrowed N, returned N" by id
        System.out.println("Core 5: counts not implemented");
    }
}
