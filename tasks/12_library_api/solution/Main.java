import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Task 12 - Expose the library (solution, Core + Stretch).
 *
 * Session 12: APIs. Builds on task 11.
 *
 * Run the server from the repo root (DATA_DIR defaults to tasks/data, PORT to 8080):
 *   mvn -q compile exec:java@task -Dtask=12_library_api/solution -Dexec.args="tasks/data 8080"
 * Then try:  curl "http://localhost:8080/books?title=the"
 * Stop it with Ctrl+C.
 *
 * Stretch, offline (prints the summary of a saved Open Library response):
 *   mvn -q compile exec:java@task -Dtask=12_library_api/solution -Dexec.args="--summarise tasks/data/openlibrary_dune.json"
 * Stretch, online:
 *   mvn -q compile exec:java@task -Dtask=12_library_api/solution -Dexec.args="--lookup Dune"
 */
public class Main {

    static final int DEFAULT_PORT = 8080;
    static final LocalDate TODAY = LocalDate.of(2026, 10, 12);

    public static void main(String[] args) throws IOException {
        if (args.length == 2 && args[0].equals("--summarise")) {
            String text = Files.readString(Path.of(args[1]), StandardCharsets.UTF_8);
            OpenLibrary.summarise(text).forEach(System.out::println);
            return;
        }
        if (args.length == 2 && args[0].equals("--lookup")) {
            try {
                OpenLibrary.summarise(OpenLibrary.search(args[1], 3)).forEach(System.out::println);
            } catch (IOException | InterruptedException error) {
                System.out.println("Open Library did not answer: " + error.getMessage());
            }
            return;
        }

        Path data = Path.of(args.length > 0 ? args[0] : "tasks/data");
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        // Every start loads a fresh library from the CSV files, so every run of the check sees the same data
        LibraryApi api = new LibraryApi(Storage.loadAll(data), TODAY);
        ApiServer server = new ApiServer(api, port);
        server.start();
        System.out.println("Library API on http://localhost:" + server.port() + " (today is " + TODAY + ")");
    }
}
