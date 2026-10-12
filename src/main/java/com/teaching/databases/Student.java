package com.teaching.databases;

/**
 * Plain data carrier for a 'students' table row.
 *
 * This is a databases-domain POJO - distinct from oop.Student.
 * The key difference: this class has an 'id' field that maps to the
 * database primary key. id == 0 means "not yet persisted".
 *
 * Design note: keeping the model free of JDBC imports (no ResultSet here)
 * lets the same class be reused in a web layer, test layer, etc.
 * The DAO (StudentDaoImpl) owns the mapping from ResultSet -> Student.
 */
public class Student {

    private int    id;      // 0 until INSERT assigns a generated primary key
    private String name;
    private int    age;
    private String grade;

    /**
     * Constructor for a new, unsaved student.
     * Use StudentDao.create(student) to persist it and get back the assigned id.
     */
    public Student(String name, int age, String grade) {
        this.id    = 0;
        this.name  = name;
        this.age   = age;
        this.grade = grade;
    }

    // ----- Getters ----

    public int    getId()    { return id; }
    public String getName()  { return name; }
    public int    getAge()   { return age; }
    public String getGrade() { return grade; }

    // ----- Setters -----

    /** Called by the DAO after a successful INSERT to record the generated key. */
    public void setId(int id)          { this.id    = id; }
    public void setName(String name)   { this.name  = name; }
    public void setAge(int age)        { this.age   = age; }
    public void setGrade(String grade) { this.grade = grade; }

    @Override
    public String toString() {
        return String.format("Student{id=%d, name='%s', age=%d, grade='%s'}",
                             id, name, age, grade);
    }
}
