import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Task 12 - the library over HTTP, JSON in and out.
 *
 * com.sun.net.httpserver.HttpServer is part of the JDK: no framework, no download.
 * Core endpoints:
 *   GET  /books              every book, by id
 *   GET  /books?title=TEXT   books whose title contains TEXT (any case)
 *   POST /loans              {"member_id": 101, "book_id": 4}
 * Stretch endpoints:
 *   GET  /books/ID               one book
 *   GET  /books/ID/openlibrary   Open Library's first match for it
 * Anything else is a JSON 404.
 */
public class ApiServer {

    private final LibraryApi api;
    private final HttpServer server;

    public ApiServer(LibraryApi api, int port) throws IOException {
        this.api = api;
        server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        // One context per path. A context matches its path and everything below it,
        // so "/" catches whatever the other two do not.
        server.createContext("/books", this::books);
        server.createContext("/loans", this::loans);
        server.createContext("/", exchange -> send(exchange, LibraryApi.error(404, "not found")));
        server.setExecutor(Executors.newFixedThreadPool(4));    // up to 4 requests at once
    }

    public void start() {
        server.start();
    }

    public int port() {
        return server.getAddress().getPort();
    }

    /** The non-empty parts of a path: "/books/4/" gives [books, 4]. */
    static List<String> parts(String path) {
        List<String> parts = new ArrayList<>();
        for (String part : path.split("/")) {
            if (!part.isEmpty()) {
                parts.add(part);
            }
        }
        return parts;
    }

    /** The value of one query parameter, decoded: "war%20of" gives "war of". Null when missing. */
    static String queryValue(String rawQuery, String name) {
        if (rawQuery == null) {
            return null;
        }
        for (String pair : rawQuery.split("&")) {
            int equals = pair.indexOf('=');
            String key = equals < 0 ? pair : pair.substring(0, equals);
            if (URLDecoder.decode(key, StandardCharsets.UTF_8).equals(name)) {
                return equals < 0 ? "" : URLDecoder.decode(pair.substring(equals + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    static boolean isNumber(String text) {
        return text.matches("[0-9]{1,9}");     // up to 9 digits always fits in an int
    }

    private void books(HttpExchange exchange) throws IOException {
        List<String> parts = parts(exchange.getRequestURI().getRawPath());
        boolean get = exchange.getRequestMethod().equals("GET");
        LibraryApi.Response response;
        if (get && parts.equals(List.of("books"))) {
            response = api.listBooks(queryValue(exchange.getRequestURI().getRawQuery(), "title"));
        } else if (get && parts.size() == 2 && isNumber(parts.get(1))) {
            response = api.getBook(Integer.parseInt(parts.get(1)));
        } else if (get && parts.size() == 3 && isNumber(parts.get(1)) && parts.get(2).equals("openlibrary")) {
            response = api.openLibrary(Integer.parseInt(parts.get(1)));
        } else {
            response = LibraryApi.error(404, "not found");
        }
        send(exchange, response);
    }

    private void loans(HttpExchange exchange) throws IOException {
        List<String> parts = parts(exchange.getRequestURI().getRawPath());
        LibraryApi.Response response;
        if (exchange.getRequestMethod().equals("POST") && parts.equals(List.of("loans"))) {
            byte[] body = exchange.getRequestBody().readAllBytes();
            response = api.createLoan(body);
        } else {
            response = LibraryApi.error(404, "not found");
        }
        send(exchange, response);
    }

    /** Every response, errors included, is JSON with its length in bytes. */
    private static void send(HttpExchange exchange, LibraryApi.Response response) throws IOException {
        byte[] bytes = response.json().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(response.status(), bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
        System.err.println(exchange.getRequestMethod() + " " + exchange.getRequestURI() + " -> " + response.status());
    }
}
