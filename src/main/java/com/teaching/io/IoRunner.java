package com.teaching.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Demonstrates Java NIO2 file I/O: Path, Files API, BufferedReader/Writer,
 * and the EAFP vs LBYL debate in an IO context.
 *
 * All demos use temp files so no external setup is required - run as-is.
 *
 * EAFP/LBYL content moved here from ExceptionDemo (source repo):
 * it belongs in IO because the debate is specifically about how to handle
 * file-access failures, not about exception mechanics in general.
 */
public class IoRunner {

    public static void main(String[] args) {
        pathDemo();
        writeAndReadDemo();
        bufferedRwDemo();
        eafpVsLbylDemo();
    }

    // -------------------------------------------------------------------------
    // 1. Path - represent and inspect file system locations without touching disk
    // -------------------------------------------------------------------------

    static void pathDemo() {
        System.out.println("=== Path (NIO2) ===");

        // Path.of() is the modern replacement for Paths.get() (same behaviour)
        Path relative = Path.of("data", "reports", "output.txt");
        System.out.println("path          : " + relative);
        System.out.println("getFileName() : " + relative.getFileName());  // output.txt
        System.out.println("getParent()   : " + relative.getParent());    // data/reports
        System.out.println("toAbsolute()  : " + relative.toAbsolutePath());

        // resolve() appends a child path to a base - OS-separator-safe
        Path base     = Path.of("data");
        Path resolved = base.resolve("reports").resolve("output.txt");
        System.out.println("resolved      : " + resolved);

        // Use the system temp dir to show real file-property checks
        Path tmpDir = Path.of(System.getProperty("java.io.tmpdir"));
        System.out.println("tmpDir exists      : " + Files.exists(tmpDir));
        System.out.println("tmpDir isDirectory : " + Files.isDirectory(tmpDir));
        System.out.println("tmpDir isReadable  : " + Files.isReadable(tmpDir));
    }

    // -------------------------------------------------------------------------
    // 2. Files.writeString / readString (Java 11+) - simplest read/write API
    // -------------------------------------------------------------------------

    static void writeAndReadDemo() {
        System.out.println("\n=== Files.writeString / readString ===");
        Path file = null;
        try {
            file = Files.createTempFile("teaching-io-", ".txt");

            String content = "Hello from NIO2\nSecond line\nThird line";

            // One call: opens, writes, closes - no try-with-resources needed here
            Files.writeString(file, content);
            System.out.println("Written: " + file.getFileName());

            // readString: entire file as one String
            String read = Files.readString(file);
            System.out.println("readString:\n" + read);

            // readAllLines: entire file as List<String> - all lines in memory at once
            List<String> lines = Files.readAllLines(file);
            System.out.println("Line count: " + lines.size());

            // Files.lines: lazy Stream - one line at a time; MUST be closed
            System.out.println("Stream filter (non-blank lines):");
            try (Stream<String> stream = Files.lines(file)) {
                stream.filter(l -> !l.isBlank())
                      .map(String::toUpperCase)
                      .forEach(l -> System.out.println("  " + l));
            }
            // If Files.lines is not closed, the underlying file handle leaks.
            // try-with-resources ensures close() even if the stream throws.

        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        } finally {
            deleteSilently(file);
        }
    }

    // -------------------------------------------------------------------------
    // 3. BufferedReader / BufferedWriter - explicit buffered I/O
    //    Use when you need line-by-line control or are writing many small chunks.
    // -------------------------------------------------------------------------

    static void bufferedRwDemo() {
        System.out.println("\n=== BufferedReader / BufferedWriter ===");
        Path file = null;
        try {
            file = Files.createTempFile("teaching-bw-", ".txt");

            // try-with-resources: close() is called automatically on normal exit OR exception
            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                writer.write("Line A");
                writer.newLine();   // platform-correct line separator (\n or \r\n)
                writer.write("Line B");
                writer.newLine();
                writer.write("Line C");
            }   // <-- writer.close() called here regardless of what happened above

            try (BufferedReader reader = Files.newBufferedReader(file)) {
                String line;
                // readLine() returns null at end-of-file, not an exception
                while ((line = reader.readLine()) != null) {
                    System.out.println("Read: " + line);
                }
            }   // <-- reader.close() called here

        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        } finally {
            deleteSilently(file);
        }
    }

    // -------------------------------------------------------------------------
    // 4. EAFP vs LBYL in IO context
    //
    //    LBYL (Look Before You Leap): check preconditions before acting.
    //    EAFP (Easier to Ask Forgiveness than Permission): just try it; catch failure.
    //
    //    In IO, LBYL has a fundamental flaw: TOCTOU race condition.
    //    Between the check and the read, the file state can change.
    //    EAFP avoids this and is the idiomatic Java approach.
    // -------------------------------------------------------------------------

    static void eafpVsLbylDemo() {
        System.out.println("\n=== EAFP vs LBYL ===");

        Path missing = Path.of("this-file-does-not-exist.txt");
        Path present = null;

        try {
            present = Files.createTempFile("teaching-eafp-", ".txt");
            Files.writeString(present, "readable content");

            lbylRead(missing);   // file absent - takes the else branch
            lbylRead(present);   // file present - reads successfully

            eafpRead(missing);   // catches NoSuchFileException
            eafpRead(present);   // reads successfully

        } catch (IOException e) {
            System.out.println("Setup error: " + e.getMessage());
        } finally {
            deleteSilently(present);
        }
    }

    static void lbylRead(Path path) {
        System.out.println("\n[LBYL] " + path.getFileName());
        // Race condition: between Files.exists() and Files.readString() another
        // process could delete the file. The catch block is still required.
        if (Files.exists(path)) {
            try {
                System.out.println("  content: " + Files.readString(path));
            } catch (IOException e) {
                System.out.println("  failed after exists() check: " + e.getMessage());
            }
        } else {
            System.out.println("  file does not exist - skipping");
        }
    }

    static void eafpRead(Path path) {
        System.out.println("\n[EAFP] " + path.getFileName());
        try {
            System.out.println("  content: " + Files.readString(path));

        } catch (NoSuchFileException e) {
            // Specific subtype of IOException - lets you differentiate "not found"
            // from other IO failures (permissions, disk errors, etc.)
            System.out.println("  not found: " + e.getFile());

        } catch (AccessDeniedException e) {
            System.out.println("  permission denied: " + e.getFile());

        } catch (IOException e) {
            System.out.println("  IO error: " + e.getMessage());
        }
        // No precondition check - no race condition. Idiomatic Java.
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    static void deleteSilently(Path path) {
        if (path != null) {
            try { Files.deleteIfExists(path); } catch (IOException ignored) {}
        }
    }
}
