import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Task 07 - Errors done properly (starter).
 *
 * The book, member, policy and library classes are the task 06 solution. The
 * exception classes are stubs. Make Library.borrow and Library.returnBook throw
 * them instead of returning false, then write the command loop below.
 * Fill in each part marked TODO, then run the check (see tasks/README.md).
 *
 * Run from the repo root (the commands come from standard input):
 *   mvn -q compile exec:java@task -Dtask=07_errors/starter < tasks/data/input_07.txt
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

    /**
     * TODO Core 5: "4" becomes 4; anything that is not an optional "-" and the
     * digits 0 to 9 throws CommandException("member id must be a whole number, got 'TEXT'").
     */
    static int parseId(String text, String what) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * TODO Core 3 and 5: do one command (borrow, return, loans) and return the line
     * to print; throw a LibraryException subclass when it cannot be done.
     */
    static String runCommand(Library library, String[] words) {
        throw new UnsupportedOperationException("not implemented");
    }

    public static void main(String[] args) throws IOException {
        Library library = loadLibrary();
        System.out.println("Library Manager: " + library.bookCount() + " books, " + library.memberCount() + " members");
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        // TODO Core 3 to 6: read lines until "quit" or the end of the input, skip blank
        // lines, echo "> WORDS", run each command, print "Error: MESSAGE" for a
        // LibraryException and "Error: unexpected problem" for anything else.
        // Always finish with "Goodbye".
        System.out.println("Core 3 to 6: command loop not implemented");
    }
}
