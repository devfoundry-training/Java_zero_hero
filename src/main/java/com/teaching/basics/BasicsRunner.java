package com.teaching.basics;

/**
 * Entry point for the basics module.
 * Demonstrates Java's most fundamental building blocks: output, variables, type inference.
 *
 * Original Main.java (source repo) contained three issues fixed here:
 *   1. A public static String field mutated by every method call - global mutable state is an anti-pattern.
 *      Each method should use local scope or accept parameters.
 *   2. System.out.printf("%s %s this is the ans") with no arguments supplied - throws
 *      MissingFormatArgumentException at runtime. Always match format specifiers to arguments.
 *   3. Cross-domain coupling: it imported ConditionsExtended (control flow) into a basics class.
 *      Each domain should be independently runnable.
 */
public class BasicsRunner {

    public static void main(String[] args) {
        System.out.println("Hello, Java!");

        // var uses local type inference - the compiler resolves the type at compile time.
        // var is NOT dynamic typing; the type is fixed after assignment.
        var message = "Zero to Hero";
        int year = 2024;
        System.out.println(message + " - Java " + year);

        // printf: %s = String placeholder, %d = integer, %n = platform-correct newline
        String name = "Learner";
        System.out.printf("Welcome, %s! Running on Java %d.%n", name, year);
    }
}
