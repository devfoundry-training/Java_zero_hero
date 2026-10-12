import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Task 12 Stretch - consuming a public API: Open Library search.
 *   https://openlibrary.org/search.json?title=dune
 *
 * Two halves, kept apart on purpose:
 *   - summarise() turns response TEXT into lines. No network, so it is checked
 *     against the recorded data/openlibrary_dune.json
 *   - search() does the HTTP call. It needs the network, so it is never part of a check
 * Any field can be missing from a real response, so every read has a fallback.
 */
public final class OpenLibrary {

    static final String SEARCH_URL = "https://openlibrary.org/search.json";
    static final String FIELDS = "key,title,author_name,first_publish_year,edition_count";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private OpenLibrary() {
        // static members only: no objects of this class
    }

    /**
     * Returns the raw JSON text. Throws IOException when the network or Open
     * Library lets us down, including a timeout after 10 seconds.
     */
    public static String search(String title, int limit) throws IOException, InterruptedException {
        String url = SEARCH_URL + "?title=" + URLEncoder.encode(title, StandardCharsets.UTF_8)
                + "&limit=" + limit + "&fields=" + FIELDS;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "ZeroToHeroLibrary/1.0 (teaching example)")
                .GET()
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IOException("Open Library answered " + response.statusCode());
        }
        return response.body();
    }

    /** The docs array of a response, or an empty list. */
    private static List<?> docs(Map<?, ?> document) {
        return document.get("docs") instanceof List<?> list ? list : List.of();
    }

    private static List<String> authors(Map<?, ?> doc) {
        List<String> names = new ArrayList<>();
        if (doc.get("author_name") instanceof List<?> list) {
            for (Object name : list) {
                names.add(String.valueOf(name));
            }
        }
        return names;
    }

    /** The first result as a small JSON object for the API, or null when there is none. */
    public static Map<String, Object> firstMatch(String text) {
        if (!(Json.parse(text) instanceof Map<?, ?> document) || docs(document).isEmpty()) {
            return null;
        }
        Map<?, ?> doc = (Map<?, ?>) docs(document).get(0);
        Map<String, Object> match = new LinkedHashMap<>();
        match.put("key", doc.get("key"));
        match.put("title", doc.get("title"));
        match.put("authors", authors(doc));
        match.put("first_publish_year", doc.get("first_publish_year"));
        match.put("edition_count", doc.get("edition_count"));
        return match;
    }

    /** Lines describing a search response. */
    public static List<String> summarise(String text) {
        Map<?, ?> document = (Map<?, ?>) Json.parse(text);
        List<String> lines = new ArrayList<>();
        lines.add("Open Library search: " + valueOr(document.get("numFound"), 0) + " results");
        int number = 1;
        for (Object item : docs(document)) {
            Map<?, ?> doc = (Map<?, ?>) item;
            List<String> authors = authors(doc);
            String byLine = authors.isEmpty() ? "unknown author" : String.join(", ", authors);
            lines.add(number + ". " + valueOr(doc.get("title"), "untitled") + " by " + byLine
                    + ", first published " + valueOr(doc.get("first_publish_year"), "unknown")
                    + ", " + valueOr(doc.get("edition_count"), 0) + " editions"
                    + ", " + valueOr(doc.get("key"), "no key"));
            number++;
        }
        return lines;
    }

    private static Object valueOr(Object value, Object fallback) {
        return value == null ? fallback : value;
    }
}
