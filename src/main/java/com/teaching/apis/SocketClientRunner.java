package com.teaching.apis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Minimal TCP client - connects to SocketServerRunner, sends three messages, prints echoes.
 *
 * HOW TO RUN:
 *   Start SocketServerRunner first (Terminal 1), then run this (Terminal 2):
 *   mvn exec:java -Dexec.mainClass="com.teaching.apis.SocketClientRunner"
 *
 * Key concepts:
 *   Socket(host, port) - connects to the server; throws ConnectException if nothing is listening
 *   getOutputStream() - bytes you write here travel to the server's InputStream
 *   getInputStream() - bytes the server writes arrive here
 *   The two streams are independent: full-duplex communication
 */
public class SocketClientRunner {

    public static void main(String[] args) {
        String host = "localhost";
        int    port = SocketServerRunner.PORT;   // reuse the constant - no magic numbers

        System.out.println("Connecting to " + host + ":" + port + " ...");

        try (Socket         socket = new Socket(host, port);
             PrintWriter    out    = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in     = new BufferedReader(
                                        new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected.\n");

            String[] messages = { "Hello", "from the client", "quit" };

            for (String msg : messages) {
                System.out.println("  sending  : " + msg);
                out.println(msg);               // send a line - server's readLine() unblocks

                String reply = in.readLine();   // block until the server echoes back
                System.out.println("  received : " + reply);
            }

        } catch (ConnectException e) {
            // Specific subtype of IOException - server not running or wrong port
            System.out.println("Could not connect: is SocketServerRunner running on port " + port + "?");
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }

        System.out.println("\nConnection closed.");
    }
}
