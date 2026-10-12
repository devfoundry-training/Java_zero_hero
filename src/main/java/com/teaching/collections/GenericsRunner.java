package com.teaching.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates generics safety using the canonical Box<T> class.
 *
 * Three teaching sections:
 *   1. Safe generic usage - compiler enforces type correctness at compile time
 *   2. Raw type (unsafe) - bypasses generics; causes "heap pollution";
 *                              compiler emits an unchecked warning
 *   3. ClassCastException - the runtime consequence of heap pollution;
 *                              demonstrates WHY the warning must not be ignored
 *
 * Historical context:
 *   Generics were added in Java 5 (2004). Before that, ALL collections used
 *   Object, and programmers had to cast manually (and hope for the best).
 *   Raw types exist only for backwards compatibility with pre-Java-5 code.
 *   New code should NEVER use raw types.
 *
 * Run:
 *   mvn exec:java -Dexec.mainClass="com.teaching.collections.GenericsRunner"
 */
public class GenericsRunner {

    public static void main(String[] args) {
        safeGenericDemo();
        rawTypeDemo();
        classcastExceptionDemo();
        boundedTypeDemo();
    }

    // -------------------------------------------------------------------------
    // 1. Safe generic usage - the right way
    // -------------------------------------------------------------------------

    static void safeGenericDemo() {
        System.out.println("=== Safe Generic Usage ===");

        // The type parameter <String> is locked in at compile time.
        // The compiler guarantees that only Strings go in and come out.
        Box<String> stringBox = new Box<>("hello");
        String value = stringBox.getValue();   // No cast needed - type is known
        System.out.println("  Box<String>: " + stringBox + "  value=" + value);

        Box<Integer> intBox = new Box<>(42);
        int number = intBox.getValue();        // auto-unboxed Integer -> int
        System.out.println("  Box<Integer>: " + intBox + "  value=" + number);

        // Attempting to store the wrong type is a compile-time error:
        // stringBox.setValue(123);  // <- Does not compile: incompatible types

        // Generic collections: List<T> is the standard example
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        // names.add(42);   // <- compile error - int is not a String
        System.out.println("  List<String>: " + names);
    }

    // -------------------------------------------------------------------------
    // 2. Raw type - pre-Java-5 style; NEVER use in new code
    // -------------------------------------------------------------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void rawTypeDemo() {
        System.out.println("\n=== Raw Type (unsafe - for educational demo only) ===");
        System.out.println("  @SuppressWarnings used here to suppress the compiler warning");
        System.out.println("  so the demo compiles. In real code, treat the warning as an error.");

        // Raw Box (no type parameter) - the compiler no longer checks the type.
        // Box rawBox is equivalent to Box<Object> at runtime but with no compile-time safety.
        Box rawBox = new Box("initially a String");
        System.out.println("  rawBox after new Box(\"...\")  : " + rawBox);

        // The compiler emits: unchecked call to setValue(T) as a member of raw type Box
        // We suppress it here - but in production code, do NOT suppress this warning.
        rawBox.setValue(9999);   // Heap pollution: the box now holds an Integer,
                                 // but nothing stops us from also calling it a Box<String>
        System.out.println("  rawBox after setValue(9999) : " + rawBox);

        // The problem becomes visible in the next demo (classcastExceptionDemo).
    }

    // -------------------------------------------------------------------------
    // 3. ClassCastException - the runtime consequence of raw types
    //    This is why the compiler issues the "unchecked" warning.
    // -------------------------------------------------------------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void classcastExceptionDemo() {
        System.out.println("\n=== ClassCastException from Raw Type (heap pollution) ===");

        // Step 1: create a properly typed Box<String>
        Box<String> typedBox = new Box<>("I am a String");

        // Step 2: assign it to a raw reference (loses type information)
        //         This is what happens when old code (raw-type) interacts with new generics code.
        Box rawRef = typedBox;   // raw assignment - no cast, no warning (widening)

        // Step 3: through the raw reference, store an Integer
        //         The compiler warns: unchecked call to setValue(T)
        rawRef.setValue(42);   // Heap pollution! The object "behind" typedBox now holds an Integer,
                               // even though typedBox is declared as Box<String>.

        // Step 4: read through the typed reference - the compiler inserts an implicit cast
        //         to String (because typedBox is Box<String>). That cast fails at runtime.
        System.out.println("  typedBox is declared as Box<String>");
        System.out.println("  rawRef.setValue(42) stored an Integer through the raw reference");
        System.out.println("  Now reading typedBox.getValue() - compiler inserts implicit (String) cast...");
        try {
            String s = typedBox.getValue();   // ClassCastException: Integer cannot be cast to String
            System.out.println("  value = " + s);   // never reached
        } catch (ClassCastException e) {
            System.out.println("  ClassCastException: " + e.getMessage());
            System.out.println("  The cast failed at RUNTIME, not compile time.");
            System.out.println("  The 'unchecked' warning was the only compile-time hint something was wrong.");
        }

        System.out.println();
        System.out.println("  Summary:");
        System.out.println("    Raw type  -> compiler skips type check -> heap pollution possible");
        System.out.println("    Generic   -> compiler checks at every put/get -> ClassCastException impossible");
        System.out.println("    Rule: if you see an 'unchecked' warning, do NOT suppress it - fix the root cause.");
    }

    // -------------------------------------------------------------------------
    // 4. Bounded type parameters - restrict what T can be
    // -------------------------------------------------------------------------

    static void boundedTypeDemo() {
        System.out.println("\n=== Bounded Type Parameter ===");

        // <T extends Number> means T must be Number or a subclass (Integer, Double, etc.)
        System.out.println("  sumOfBox(new Box<Integer>(10)) = " + sumOfBox(new Box<>(10)));
        System.out.println("  sumOfBox(new Box<Double>(3.14)) = " + sumOfBox(new Box<>(3.14)));

        // sumOfBox(new Box<String>("hi"))  // <- compile error: String is not a Number
    }

    /**
     * Bounded type parameter: T extends Number.
     * Enables calling Number methods (doubleValue, intValue, etc.) on the value.
     * Without the bound, T is treated as Object and Number methods are unavailable.
     */
    private static <T extends Number> double sumOfBox(Box<T> box) {
        return box.getValue().doubleValue();
    }
}
