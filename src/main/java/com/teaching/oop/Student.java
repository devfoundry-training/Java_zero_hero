package com.teaching.oop;

/**
 * A plain student model class demonstrating encapsulation: private fields, a constructor,
 * getters, and a meaningful toString().
 *
 * Fully rewritten from source repo (teaching moment / anti-pattern corrected):
 *   The original Student extended Shape, which required implementing getArea() returning 0.
 *   Student is not a geometric shape - this was a live coding mistake where the wrong
 *   base class was selected. Correct approach: Student is an independent class with
 *   its own relevant fields (name, age, grade).
 */
public class Student {

    private final String name;
    private final int age;
    private String grade;

    public Student(String name, int age, String grade) {
        this.name = name;
        this.age = age;
        this.grade = grade;
    }

    public String getName()  { return name; }
    public int    getAge()   { return age; }
    public String getGrade() { return grade; }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return String.format("Student[name=%s, age=%d, grade=%s]", name, age, grade);
    }
}
