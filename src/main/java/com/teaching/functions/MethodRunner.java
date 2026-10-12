package com.teaching.functions;

/**
 * Demonstrates method signatures, return types, overloading, pass-by-value, and scope.
 *
 * The original Main.java (source repo) used a public static String field mutated by every
 * method - a global state anti-pattern. Methods here use local scope and parameters correctly.
 */
public class MethodRunner {

    public static void main(String[] args) {
        returnTypesDemo();
        overloadingDemo();
        passByValueDemo();
        scopeDemo();
    }

    static void returnTypesDemo() {
        System.out.println("=== Return Types ===");

        greet("Alice");                          // void: produces output, returns nothing

        int sum = add(3, 5);                     // returns a value the caller assigns
        System.out.println("3 + 5 = " + sum);

        System.out.println(describe("Bob", 25)); // return value used directly in println
    }

    static void overloadingDemo() {
        System.out.println("\n=== Overloading (same name, different parameter list) ===");
        // The compiler selects the correct version at compile time based on argument types.
        System.out.println(add(2, 3));            // int version
        System.out.println(add(2.5, 3.5));        // double version
        System.out.println(add(1, 2, 3));         // three-arg version
        System.out.println(add("Hello", "!"));    // String version
    }

    static void passByValueDemo() {
        System.out.println("\n=== Pass-by-Value ===");

        // Primitives: a COPY of the value is passed. The original is unaffected.
        int x = 10;
        doubleIt(x);
        System.out.println("After doubleIt(x) : x = " + x);   // still 10

        // Arrays/Objects: the REFERENCE is copied, but both copies point to the same object.
        // Mutating the object's contents is visible to the caller.
        int[] arr = { 1, 2, 3 };
        zeroFirstElement(arr);
        System.out.println("After zeroFirstElement : arr[0] = " + arr[0]); // 0
    }

    static void scopeDemo() {
        System.out.println("\n=== Scope ===");
        int x = 5;
        {
            // Inner block can read x from the enclosing scope.
            int y = 10;
            System.out.println("Inside block  : x=" + x + ", y=" + y);
        }
        // y is not accessible here - its scope ended with the block.
        System.out.println("Outside block : x=" + x);
    }

    // --- Methods used by the demos above ---

    static void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }

    // Overloaded: the method name is the same; the parameter types differ.
    static int    add(int a, int b)       { return a + b; }
    static double add(double a, double b) { return a + b; }
    static int    add(int a, int b, int c){ return a + b + c; }
    static String add(String a, String b) { return a + b; }

    static String describe(String name, int age) {
        return String.format("Name: %s, Age: %d", name, age);
    }

    static void doubleIt(int n) {
        n = n * 2;  // modifies the local copy only; caller's variable is unchanged
    }

    static void zeroFirstElement(int[] arr) {
        arr[0] = 0;  // modifies the shared array object; visible to the caller
    }
}
