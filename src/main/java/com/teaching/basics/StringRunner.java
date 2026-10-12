package com.teaching.basics;

/**
 * Demonstrates core String API and StringBuilder.
 *
 * Key rule: String is immutable. Every method that appears to "modify" a String
 * returns a NEW String - the original is unchanged. Ignoring the return value
 * is a silent bug.
 */
public class StringRunner {

    public static void main(String[] args) {
        equalityDemo();
        immutabilityDemo();
        apiDemo();
        stringBuilderDemo();
    }

    static void equalityDemo() {
        System.out.println("=== Equality ===");

        // new String(...) creates a distinct object in heap memory.
        // == compares references (memory addresses), not content.
        String a = new String("test");
        String b = new String("test");
        System.out.println("a == b      : " + (a == b));       // false - different objects
        System.out.println("a.equals(b) : " + a.equals(b));    // true - same content

        // Null-safe pattern: place the known non-null literal on the left.
        // "test".equals(null) returns false; null.equals(...) throws NullPointerException.
        String mightBeNull = null;
        System.out.println("null-safe   : " + "test".equals(mightBeNull)); // false, no NPE
    }

    static void immutabilityDemo() {
        System.out.println("\n=== Immutability ===");
        String word = "Programming";

        word.toUpperCase();                             // return value discarded - word is unchanged!
        System.out.println("Original          : " + word);
        System.out.println("toUpperCase()     : " + word.toUpperCase());  // new String returned
        System.out.println("Original (still)  : " + word);               // unchanged
    }

    static void apiDemo() {
        System.out.println("\n=== Common String Methods ===");
        String word = "Programming";

        System.out.println("substring(3)      : " + word.substring(3));        // "gramming"
        System.out.println("substring(3, 6)   : " + word.substring(3, 6));     // "gra"
        System.out.println("charAt(1)         : " + word.charAt(1));            // 'r'
        System.out.println("contains(\"gram\") : " + word.contains("gram"));   // true
        System.out.println("indexOf(\"gram\")  : " + word.indexOf("gram"));    // 3
        System.out.println("trim()            : " + "  spaces  ".trim());
        System.out.println("replace digits    : " + "abc123".replaceAll("[0-9]", "#"));
    }

    static void stringBuilderDemo() {
        System.out.println("\n=== StringBuilder ===");

        // String concatenation inside a loop creates many intermediate String objects.
        // StringBuilder is mutable - use it when building strings iteratively.
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i).append(" ");   // chained calls - each returns the same StringBuilder
        }
        System.out.println("Built   : " + sb);

        sb.insert(1, "!");
        System.out.println("insert  : " + sb);

        sb.replace(0, 2, "0");
        System.out.println("replace : " + sb);

        System.out.println("String  : " + sb.toString());  // convert back to immutable String
    }
}
