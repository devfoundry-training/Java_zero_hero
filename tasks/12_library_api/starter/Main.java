import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Task 12 - Expose the library (starter).
 *
 * The library classes, reservations and Storage are the task 11 solution.
 * LibraryApi.java and OpenLibrary.java are stubs. Fill in each part marked
 * TODO, then run the check (see tasks/README.md).
 *
 * Run from the repo root (DATA_DIR defaults to tasks/data, PORT to 8080):
 *   mvn -q compile exec:java@task -Dtask=12_library_api/starter -Dexec.args="tasks/data 8080"
 */
public class Main {

    static final int DEFAULT_PORT = 8080;
    static final LocalDate TODAY = LocalDate.of(2026, 10, 12);

    public static void main(String[] args) throws IOException {
        Path data = Path.of(args.length > 0 ? args[0] : "tasks/data");
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        LibraryApi api = new LibraryApi(Storage.loadAll(data), TODAY);
        // TODO Core 2: start com.sun.net.httpserver.HttpServer on localhost:port,
        // one context per path ("/books", "/loans") and "/" for the JSON 404.
        // Every response, errors included, is Content-Type: application/json.
        System.out.println("Core 2: the server on port " + port + " is not implemented");
    }
}
