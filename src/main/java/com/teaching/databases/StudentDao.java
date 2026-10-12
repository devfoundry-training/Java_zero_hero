package com.teaching.databases;

import java.sql.SQLException;
import java.util.List;

/**
 * DAO (Data Access Object) interface for the 'students' table.
 *
 * Why an interface?
 * - Decouples the caller from the JDBC implementation.
 * - In tests you can swap in an in-memory List-backed fake without any SQL.
 * - In production you could swap the H2 impl for a MySQL, PostgreSQL, or
 *     JPA-backed impl with zero changes to the callers.
 *
 * All methods declare throws SQLException because JDBC is a checked-exception API.
 * The caller (JdbcRunner) decides how to handle or re-throw.
 */
public interface StudentDao {

    /**
     * Inserts a new row. Sets student.id to the database-generated primary key.
     * Returns the same student instance (mutated with the new id) for chaining.
     */
    Student       create(Student student) throws SQLException;

    /**
     * Returns the student with the given primary key, or null if not found.
     */
    Student       findById(int id)        throws SQLException;

    /**
     * Returns all rows ordered by id ascending. Returns an empty list (not null)
     * if the table is empty.
     */
    List<Student> findAll()               throws SQLException;

    /**
     * Updates the name, age, and grade of the row matching student.id.
     * No-op (0 rows affected) if the id does not exist.
     */
    void          update(Student student) throws SQLException;

    /**
     * Deletes the row with the given primary key.
     * No-op if the id does not exist.
     */
    void          delete(int id)          throws SQLException;
}
