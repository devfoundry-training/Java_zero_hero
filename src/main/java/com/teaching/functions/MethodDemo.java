package com.teaching.functions;

/**
 * A simple class demonstrating instance fields, a constructor, and overloaded getters.
 *
 * Anti-pattern fixed from source repo (shown live as a teaching moment):
 *   The original declared 'public static String name' - a class-level field mutated
 *   by the constructor via MethodDemo.name = name. This meant every instance shared
 *   the same name, and the last constructor call would overwrite all previous ones.
 *   Correct approach: name is an instance field (private, no static modifier).
 */
public class MethodDemo {

    private final String name;  // instance field - unique per object
    private final int age;

    public MethodDemo(String name, int age) {
        this.name = name;   // this.name = the field; name = the parameter
        this.age = age;
    }

    public String getName() {
        return name;
    }

    // Overloaded: same method name, different parameter signature
    public String getName(String lastName) {
        return name + " " + lastName;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return String.format("MethodDemo[name=%s, age=%d]", name, age);
    }
}
