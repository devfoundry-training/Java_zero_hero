package com.teaching.apis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Minimal TCP echo server - accepts one client, echoes every line back, then shuts down.
 *
 * HOW TO RUN (requires two terminals):
 *   Terminal 1: mvn exec:java -Dexec.mainClass="com.teaching.apis.SocketServerRunner"
 *   Terminal 2: mvn exec:java -Dexec.mainClass="com.teaching.apis.SocketClientRunner"
 *
 * For a self-contained single-JVM demo (server on a background thread), see the
 * concurrency module - that pattern is shown in ThreadRunner after Executors are introduced.
 *
 * Key concepts:
 *   ServerSocket - binds to a port; holds a backlog queue of incoming connections
 *   accept() - blocks the calling thread until a client connects; returns a Socket
 *   Socket - bidirectional channel; has an InputStream (reads) and OutputStream (writes)
 */
public class SocketServerRunner {

    public static final int PORT = 9090;

    public static void main(String[] args) {
        System.out.println("Echo server starting on port " + PORT);
        System.out.println("Waiting for one client connection, then will shut down...\n");

        // ServerSocket.close() is called automatically by try-with-resources.
        // OS releases the port binding on close.
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // accept() BLOCKS here - the thread sleeps until a client connects
            try (Socket            client = serverSocket.accept();
                 BufferedReader    in     = new BufferedReader(
                                               new InputStreamReader(client.getInputStream()));
                 PrintWriter       out    = new PrintWriter(client.getOutputStream(), true)) {
                // PrintWriter(stream, true) - autoFlush=true: each println() flushes immediately.
                // Without autoFlush, the client would block forever waiting for data.

                System.out.println("Client connected : " + client.getRemoteSocketAddress());

                String line;
                // readLine() returns null when the client closes the connection
                while ((line = in.readLine()) != null) {
                    System.out.println("  received : " + line);
                    out.println("echo> " + line);

                    if ("quit".equalsIgnoreCase(line)) {
                        System.out.println("  quit received - closing");
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }

        System.out.println("\nServer shut down.");
        // Production servers wrap accept() in a loop and hand each Socket to a thread pool,
        // so many clients can be served concurrently. That pattern is in the concurrency module.
    }
}
