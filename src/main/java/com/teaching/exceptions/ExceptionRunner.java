package com.teaching.exceptions;

/**
 * Demonstrates exception mechanics: try/catch/finally, checked vs unchecked,
 * multi-catch, custom hierarchies, try-with-resources, and rethrowing.
 *
 * Scope: exception control flow and structure only.
 * File I/O exception patterns (EAFP vs LBYL) live in com.teaching.io.IoRunner -
 * they belong in the IO context, not here.
 */
public class ExceptionRunner {

    public static void main(String[] args) {
        tryCatchFinally();
        checkedVsUnchecked();
        multiCatch();
        customHierarchyDemo();
        tryWithResourcesDemo();
        rethrowAndWrap();
    }

    // -------------------------------------------------------------------------
    // 1. try / catch / finally
    // -------------------------------------------------------------------------

    static void tryCatchFinally() {
        System.out.println("=== try / catch / finally ===");
        try {
            System.out.println("try: before risky line");
            int result = 10 / 0;                        // throws ArithmeticException
            System.out.println("try: after risky line (never reached)");
        } catch (ArithmeticException e) {
            System.out.println("catch: " + e.getMessage());   // "/ by zero"
        } finally {
            // finally runs on every exit path: normal, exception, or return
            System.out.println("finally: always runs");
        }
    }

    // -------------------------------------------------------------------------
    // 2. Checked vs Unchecked
    //    Checked   (extends Exception):         compiler forces handle-or-declare
    //    Unchecked (extends RuntimeException):  no compiler enforcement
    // -------------------------------------------------------------------------

    static void checkedVsUnchecked() {
        System.out.println("\n=== Checked vs Unchecked ===");

        // Unchecked - NullPointerException, no forced handling
        try {
            String s = null;
            s.length();
        } catch (NullPointerException e) {
            System.out.println("NPE (unchecked): " + e.getClass().getSimpleName());
        }

        // Checked - ShapeException; the compiler requires you to catch or declare it
        try {
            throwChecked();
        } catch (ShapeException e) {
            System.out.println("ShapeException (checked): " + e.getMessage());
        }
    }

    static void throwChecked() throws ShapeException {
        throw new ShapeException("Checked exception - caller must handle or declare");
    }

    // -------------------------------------------------------------------------
    // 3. Multi-catch (Java 7+) - one handler for unrelated exception types
    // -------------------------------------------------------------------------

    static void multiCatch() {
        System.out.println("\n=== Multi-catch ===");

        // Object array: null entry -> NullPointerException on .toString()
        //               "bad" entry -> NumberFormatException on parseInt
        Object[] items = { "42", null, "bad" };
        for (Object item : items) {
            try {
                int n = Integer.parseInt(item.toString());
                System.out.println("Parsed: " + n);
            } catch (NumberFormatException | NullPointerException e) {
                // Multi-catch: types must be unrelated (no parent/child relationship)
                System.out.println("Caught " + e.getClass().getSimpleName()
                    + " for item: " + item);
            }
        }
    }

    // -------------------------------------------------------------------------
    // 4. Custom exception hierarchy
    //    ShapeException -> InvalidDimensionException -> InvalidSquareException
    // -------------------------------------------------------------------------

    static void customHierarchyDemo() {
        System.out.println("\n=== Custom Hierarchy ===");

        // Catch at the most specific level
        try {
            throw new InvalidSquareException("Sides do not match: 3.0 != 4.0");
        } catch (InvalidSquareException e) {
            System.out.println("Specific catch : " + e.getClass().getSimpleName()
                + " - " + e.getMessage());
        }

        // Catching a parent type also catches all its subtypes
        try {
            throw new InvalidSquareException("Caught as parent");
        } catch (ShapeException e) {
            // InvalidSquareException IS-A ShapeException - this catch works
            System.out.println("Parent catch   : " + e.getClass().getSimpleName()
                + " - " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // 5. try-with-resources
    //    Any class implementing AutoCloseable can be managed here.
    //    close() is called in reverse order of declaration - even if an exception
    //    is thrown inside the block.
    //    File-based example is in IoRunner; here we use a plain AutoCloseable.
    // -------------------------------------------------------------------------

    static void tryWithResourcesDemo() {
        System.out.println("\n=== try-with-resources ===");

        try (ManagedResource r1 = new ManagedResource("Connection");
             ManagedResource r2 = new ManagedResource("Statement")) {
            r1.use();
            r2.use();
        }   // r2.close() then r1.close() - reverse of open order

        System.out.println("After block: both resources closed");
    }

    // -------------------------------------------------------------------------
    // 6. Rethrowing / wrapping
    //    Wrap a specific exception in a broader one to cross API boundaries.
    // -------------------------------------------------------------------------

    static void rethrowAndWrap() {
        System.out.println("\n=== Rethrow / Wrap ===");
        try {
            parseStrict("not-a-number");
        } catch (RuntimeException e) {
            System.out.println("Caught wrapper : " + e.getMessage());
            System.out.println("Root cause     : " + e.getCause().getClass().getSimpleName());
        }
    }

    static int parseStrict(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Wrapping preserves the original cause (e) for debugging
            throw new RuntimeException("Invalid integer input: " + value, e);
        }
    }

    // -------------------------------------------------------------------------
    // Supporting class - minimal AutoCloseable for the try-with-resources demo
    // -------------------------------------------------------------------------

    static class ManagedResource implements AutoCloseable {
        private final String name;

        ManagedResource(String name) {
            this.name = name;
            System.out.println("  opened  : " + name);
        }

        void use() {
            System.out.println("  using   : " + name);
        }

        @Override
        public void close() {
            System.out.println("  closed  : " + name);
        }
    }
}
