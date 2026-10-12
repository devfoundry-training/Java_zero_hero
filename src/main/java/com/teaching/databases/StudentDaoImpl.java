package com.teaching.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of StudentDao.
 *
 * Key patterns demonstrated:
 *   PreparedStatement - parameterised SQL; prevents SQL injection; compiled once
 *   RETURN_GENERATED_KEYS - retrieves the auto-increment id produced by INSERT
 *   try-with-resources - Connection, PreparedStatement, ResultSet all implement
 *                         AutoCloseable; closing them in reverse order is guaranteed
 *   mapRow() - centralised ResultSet -> Student mapping; change column
 *                         names in ONE place if the schema evolves
 *
 * Each method opens its own connection from DbConnection.getConnection().
 * H2 in-memory databases with the same name (jdbc:h2:mem:teaching) share data
 * across connections within the same JVM, so this works correctly.
 * In production you would inject (or pool) a shared DataSource instead.
 */
public class StudentDaoImpl implements StudentDao {

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public Student create(Student student) throws SQLException {
        final String sql = "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)";

        // Statement.RETURN_GENERATED_KEYS tells the driver to capture the
        // AUTO_INCREMENT value so we can read it back with getGeneratedKeys().
        try (Connection       conn = DbConnection.getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, student.getName());
            ps.setInt   (2, student.getAge());
            ps.setString(3, student.getGrade());
            ps.executeUpdate();   // returns row-count (1), not the result set

            // getGeneratedKeys() returns a ResultSet with one row per inserted row.
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    student.setId(keys.getInt(1));   // column 1 = the generated id
                }
            }
        }
        return student;   // same instance, now with id set
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public Student findById(int id) throws SQLException {
        final String sql = "SELECT id, name, age, grade FROM students WHERE id = ?";

        try (Connection       conn = DbConnection.getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                // rs.next() advances the cursor; returns false if no rows
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public List<Student> findAll() throws SQLException {
        final String sql = "SELECT id, name, age, grade FROM students ORDER BY id";

        // PreparedStatement and ResultSet both implement AutoCloseable.
        // The try-with-resources closes rs first, then ps, then conn (LIFO order).
        try (Connection       conn = DbConnection.getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql);
             ResultSet         rs   = ps.executeQuery()) {

            List<Student> students = new ArrayList<>();
            while (rs.next()) {
                students.add(mapRow(rs));
            }
            return students;   // empty list (not null) when table has no rows
        }
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public void update(Student student) throws SQLException {
        final String sql =
            "UPDATE students SET name = ?, age = ?, grade = ? WHERE id = ?";

        try (Connection       conn = DbConnection.getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setInt   (2, student.getAge());
            ps.setString(3, student.getGrade());
            ps.setInt   (4, student.getId());   // WHERE clause - must be last
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void delete(int id) throws SQLException {
        final String sql = "DELETE FROM students WHERE id = ?";

        try (Connection       conn = DbConnection.getConnection();
             PreparedStatement ps   = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Maps the current row of the ResultSet to a Student.
     * Using column names (not indices) makes the mapping resilient to SELECT
     * column reordering. Change the column name here if the schema is renamed.
     */
    private static Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student(
            rs.getString("name"),
            rs.getInt   ("age"),
            rs.getString("grade")
        );
        s.setId(rs.getInt("id"));
        return s;
    }
}
