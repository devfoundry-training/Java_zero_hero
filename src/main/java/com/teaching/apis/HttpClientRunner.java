package com.teaching.apis;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Demonstrates the Java 11+ HttpClient API: synchronous GET, response inspection,
 * and minimal JSON field extraction without an external library.
 *
 * Requires internet access. Uses JSONPlaceholder - a free, stable REST testing API.
 *
 * HOW TO RUN:
 *   mvn exec:java -Dexec.mainClass="com.teaching.apis.HttpClientRunner"
 *
 * Key concepts:
 *   HttpClient - immutable, thread-safe; build once, reuse for many requests
 *   HttpRequest - immutable value object describing a single request
 *   HttpResponse.BodyHandlers - strategy for reading the response body (String, byte[], File...)
 */
public class HttpClientRunner {

    private static final String BASE = "https://jsonplaceholder.typicode.com";

    public static void main(String[] args) {
        // HttpClient is immutable and thread-safe - share one instance across all requests
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

        syncGetDemo(client);
        responseMetadataDemo(client);
        multipleRequestsDemo(client);
    }

    // -------------------------------------------------------------------------
    // 1. Basic synchronous GET
    // -------------------------------------------------------------------------

    static void syncGetDemo(HttpClient client) {
        System.out.println("=== Synchronous GET ===");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE + "/todos/1"))
            .header("Accept", "application/json")
            .GET()      // GET is the default; explicit here for readability
            .build();

        try {
            // send() blocks until the full response is received
            // sendAsync() returns CompletableFuture<HttpResponse> - covered in concurrency module
            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status : " + response.statusCode());
            System.out.println("Body   : " + response.body());

            // Manual extraction - no JSON library.
            // extractField() below is intentionally simple and fragile:
            // it only handles flat JSON objects with no nesting or arrays.
            // In production: use Jackson (ObjectMapper) or Gson.
            String body = response.body();
            System.out.println("\nExtracted fields:");
            System.out.println("  userId    : " + extractField(body, "userId"));
            System.out.println("  id        : " + extractField(body, "id"));
            System.out.println("  title     : " + extractField(body, "title"));
            System.out.println("  completed : " + extractField(body, "completed"));

        } catch (IOException e) {
            System.out.println("Network error (are you online?): " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // restore interrupted status - never swallow this
        }
    }

    // -------------------------------------------------------------------------
    // 2. Inspecting response metadata
    // -------------------------------------------------------------------------

    static void responseMetadataDemo(HttpClient client) {
        System.out.println("\n=== Response Metadata ===");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE + "/posts/1"))
            .build();

        try {
            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status  : " + response.statusCode());
            System.out.println("URI     : " + response.uri());
            System.out.println("Version : " + response.version()); // HTTP_1_1 or HTTP_2

            // Headers: each header key maps to a List<String>
            // (HTTP allows multiple values for the same header name)
            System.out.println("Selected headers:");
            response.headers().map().entrySet().stream()
                .filter(e -> e.getKey().startsWith("content")
                          || e.getKey().startsWith("cache"))
                .forEach(e ->
                    System.out.println("  " + e.getKey() + " : " + e.getValue()));

        } catch (IOException e) {
            System.out.println("Network error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // -------------------------------------------------------------------------
    // 3. Reusing the same HttpClient for multiple requests
    //    One client - many requests. This is the intended usage pattern.
    // -------------------------------------------------------------------------

    static void multipleRequestsDemo(HttpClient client) {
        System.out.println("\n=== Multiple Requests (same client) ===");

        int[] ids = { 1, 2, 3 };
        for (int id : ids) {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/todos/" + id))
                .build();
            try {
                HttpResponse<String> res =
                    client.send(req, HttpResponse.BodyHandlers.ofString());
                String title = extractField(res.body(), "title");
                System.out.printf("  todo %d : %s%n", id, title);
            } catch (IOException e) {
                System.out.println("  todo " + id + " : error - " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Utility: extract a field value from a flat JSON object string.
    //
    // Works for: {"key":"string"} and {"key":123} and {"key":true}
    // Does NOT handle: nested objects, arrays, escaped quotes, whitespace-heavy formatting.
    // -------------------------------------------------------------------------

    static String extractField(String json, String key) {
        String search = "\"" + key + "\":";
        int keyIdx = json.indexOf(search);
        if (keyIdx == -1) return "(not found)";

        int start = keyIdx + search.length();

        // Skip optional space after colon
        while (start < json.length() && json.charAt(start) == ' ') start++;

        if (json.charAt(start) == '"') {
            // String value: content lives between the two quote characters
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        } else {
            // Primitive value (number, boolean, null): ends at comma or closing brace
            int end = json.indexOf(',', start);
            if (end == -1) end = json.indexOf('}', start);
            return json.substring(start, end).trim();
        }
    }
}
