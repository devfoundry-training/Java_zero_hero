package com.teaching.databases;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Demonstrates JDBC fundamentals using H2 in-memory: schema init, full CRUD
 * via a DAO, SQL injection awareness, and manual transaction control.
 *
 * No external database required - H2 is embedded and starts automatically.
 *
 * Run:
 *   mvn exec:java -Dexec.mainClass="com.teaching.databases.JdbcRunner"
 *
 * Key concepts:
 *   DriverManager - obtains connections from the JDBC driver (H2 here)
 *   PreparedStatement - parameterised SQL; prevents injection; re-usable
 *   ResultSet - cursor over query results; advance with rs.next()
 *   DAO pattern - isolates SQL from business logic; swappable behind interface
 *   Transaction - group of operations that succeed or fail together
 *                       controlled with setAutoCommit / commit / rollback
 */
public class JdbcRunner {

    public static void main(String[] args) throws SQLException, IOException {
        // Step 1: create the 'students' table (CREATE TABLE IF NOT EXISTS).
        // Must run before any DAO calls. Safe to call multiple times.
        DbConnection.initSchema();
        System.out.println("Schema initialised - H2 in-memory database ready.\n");

        crudDemo();
        sqlInjectionNote();
        transactionDemo();
    }

    // -------------------------------------------------------------------------
    // 1. Full CRUD walkthrough via the DAO
    // -------------------------------------------------------------------------

    static void crudDemo() throws SQLException {
        System.out.println("=== CRUD Demo ===");
        StudentDao dao = new StudentDaoImpl();

        // --- INSERT ---
        // create() executes INSERT and populates student.id with the generated key
        System.out.println("\n-- INSERT (3 students) --");
        Student alice = dao.create(new Student("Alice", 20, "A"));
        Student bob   = dao.create(new Student("Bob",   22, "B"));
        Student carol = dao.create(new Student("Carol", 21, "A"));
        System.out.println("  " + alice);
        System.out.println("  " + bob);
        System.out.println("  " + carol);

        // --- SELECT ALL ---
        System.out.println("\n-- SELECT ALL --");
        dao.findAll().forEach(s -> System.out.println("  " + s));

        // --- SELECT BY ID ---
        System.out.println("\n-- SELECT BY ID (alice.id=" + alice.getId() + ") --");
        Student found = dao.findById(alice.getId());
        System.out.println("  " + found);

        // --- UPDATE ---
        // Mutate the Java object, then persist the change
        System.out.println("\n-- UPDATE (bob: grade B -> A) --");
        bob.setGrade("A");
        dao.update(bob);
        System.out.println("  " + dao.findById(bob.getId()));

        // --- DELETE ---
        System.out.println("\n-- DELETE (carol id=" + carol.getId() + ") --");
        dao.delete(carol.getId());
        System.out.println("  Remaining rows:");
        dao.findAll().forEach(s -> System.out.println("    " + s));
    }

    // -------------------------------------------------------------------------
    // 2. SQL injection - why PreparedStatement matters
    //    (Educational - the unsafe SQL string is NEVER executed)
    // -------------------------------------------------------------------------

    static void sqlInjectionNote() {
        System.out.println("\n=== SQL Injection - PreparedStatement vs String Concatenation ===");

        String maliciousInput = "' OR '1'='1";

        // !! NEVER DO THIS in real code !!
        // String concatenation puts raw user input directly into the SQL.
        String unsafeSql = "SELECT * FROM students WHERE name = '" + maliciousInput + "'";
        System.out.println("  Unsafe SQL  : " + unsafeSql);
        System.out.println("  ^ '1'='1' is always true -> returns ALL rows regardless of name.");
        System.out.println("  ^ Attacker variant: '; DROP TABLE students; --");
        System.out.println("    Would delete the entire table in one HTTP request.");

        // PreparedStatement with positional parameters is the fix.
        // The JDBC driver escapes the value - it cannot break the SQL structure.
        System.out.println("\n  Safe SQL    : SELECT * FROM students WHERE name = ?");
        System.out.println("                ps.setString(1, userInput)");
        System.out.println("  ^ Input is treated as a literal string value, never as SQL.");
        System.out.println("  Rule: ALWAYS use PreparedStatement for any user-supplied data.");
    }

    // -------------------------------------------------------------------------
    // 3. Transactions - commit and rollback
    //
    //    By default JDBC operates in auto-commit mode:
    //    every executeUpdate() is immediately committed.
    //    setAutoCommit(false) groups subsequent statements into one atomic unit.
    //    Either commit() (all changes persist) or rollback() (none persist).
    // -------------------------------------------------------------------------

    static void transactionDemo() throws SQLException {
        System.out.println("\n=== Transaction Demo ===");
        StudentDao dao = new StudentDaoImpl();

        // --- Rollback path: simulate a failure mid-transaction ---
        System.out.println("\n-- Rollback path --");
        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);   // begin explicit transaction
            System.out.println("  auto-commit OFF - transaction started");

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)")) {
                ps.setString(1, "Dave");
                ps.setInt   (2, 19);
                ps.setString(3, "C");
                ps.executeUpdate();
                System.out.println("  Dave inserted (uncommitted - not yet visible outside this connection)");
            }

            // Something went wrong - undo everything since setAutoCommit(false)
            conn.rollback();
            System.out.println("  rollback() called - Dave's insert is undone");
        }
        // Verify: Dave must NOT appear
        System.out.print("  Students after rollback: ");
        System.out.println(dao.findAll().stream().map(Student::getName).toList());

        // --- Commit path: both inserts succeed atomically ---
        System.out.println("\n-- Commit path --");
        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)")) {

                // Insert Dave
                ps.setString(1, "Dave"); ps.setInt(2, 19); ps.setString(3, "C");
                ps.executeUpdate();

                // Insert Eve - reuse the same PreparedStatement with new params
                ps.setString(1, "Eve"); ps.setInt(2, 23); ps.setString(3, "B");
                ps.executeUpdate();

                System.out.println("  Dave and Eve inserted (uncommitted)");
            }

            conn.commit();   // both inserts land atomically
            System.out.println("  commit() called - both rows persisted");
        }
        System.out.print("  Students after commit:   ");
        dao.findAll().stream()
            .map(Student::getName)
            .forEach(n -> System.out.print(n + "  "));
        System.out.println();

        System.out.println("\n  Key rule: if any step throws before commit(), call rollback()");
        System.out.println("  in a catch block to keep the database in a consistent state.");
    }
}
