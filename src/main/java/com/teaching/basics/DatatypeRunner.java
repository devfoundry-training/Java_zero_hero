package com.teaching.basics;

/**
 * Demonstrates Java's eight primitive types, their sizes, wrapper classes,
 * and integer overflow behaviour.
 */
public class DatatypeRunner {

    public static void main(String[] args) {

        // --- Integer family (signed, two's complement) ---
        byte  b = 100;
        short s = 30_000;           // underscores in numeric literals improve readability
        int   i = 2_000_000;
        long  l = 9_000_000_000L;   // L suffix required - without it the literal is an int and overflows

        // --- Floating-point ---
        float  f = 3.14f;           // f suffix required - default floating literal is double
        double d = 3.14;            // default; prefer double over float

        // --- Other primitives ---
        char    c    = 'a';         // 16-bit unsigned Unicode code point
        boolean flag = true;

        System.out.println("=== Integer Ranges ===");
        System.out.println("int max : " + Integer.MAX_VALUE);
        System.out.println("int min : " + Integer.MIN_VALUE);

        // Overflow wraps silently - no exception, no compiler warning
        System.out.println("MAX + 1 : " + (Integer.MAX_VALUE + 1));  // wraps to MIN_VALUE
        System.out.println("MIN - 1 : " + (Integer.MIN_VALUE - 1));  // wraps to MAX_VALUE

        System.out.println("\n=== All Primitive Ranges ===");
        System.out.println("byte   : " + Byte.MIN_VALUE    + " to " + Byte.MAX_VALUE);
        System.out.println("short  : " + Short.MIN_VALUE   + " to " + Short.MAX_VALUE);
        System.out.println("int    : " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE);
        System.out.println("long   : " + Long.MIN_VALUE    + " to " + Long.MAX_VALUE);
        System.out.println("float  : " + Float.MIN_VALUE   + " to " + Float.MAX_VALUE);
        System.out.println("double : " + Double.MIN_VALUE  + " to " + Double.MAX_VALUE);
        // char stores Unicode code points - cast to int to see the numeric range
        System.out.println("char   : " + (int) Character.MIN_VALUE + " to " + (int) Character.MAX_VALUE);
    }
}
