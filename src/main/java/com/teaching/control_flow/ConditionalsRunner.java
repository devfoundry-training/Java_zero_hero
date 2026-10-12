package com.teaching.control_flow;

/**
 * Demonstrates if/else-if chains, logical operators, and ternary expressions.
 */
public class ConditionalsRunner {

    public static void main(String[] args) {
        gradeDemo();
        logicalOperatorsDemo();
        evenOddCounter();
    }

    // Original method was named "method()" - renamed to describe its purpose.
    static void gradeDemo() {
        System.out.println("=== Grade Calculator ===");
        int score = 85;

        if (score >= 90) {
            System.out.println("Grade: A");
        } else if (score >= 80) {
            System.out.println("Grade: B");
        } else if (score >= 70) {
            System.out.println("Grade: C");
        } else if (score >= 60) {
            System.out.println("Grade: D");
        } else {
            System.out.println("Grade: F");
        }

        // Ternary is concise but hard to read when nested beyond two levels. Use if/else for clarity.
        String letter = (score >= 90) ? "A" :
                        (score >= 80) ? "B" :
                        (score >= 70) ? "C" :
                        (score >= 60) ? "D" : "F";
        System.out.println("Ternary: " + letter);
    }

    static void logicalOperatorsDemo() {
        System.out.println("\n=== Logical Operators ===");
        int temp = 25;
        boolean isSunny = true;

        // && (AND): both conditions must be true
        if (temp > 20 && isSunny) {
            System.out.println("Go to the park");
        }

        // || (OR): at least one condition must be true
        if (temp < 20 || !isSunny) {
            System.out.println("Stay indoors");
        }

        // ! (NOT): inverts a boolean
        if (!(temp > 30)) {
            System.out.println("Not too hot");
        }
    }

    static void evenOddCounter() {
        System.out.println("\n=== Even/Odd Counter ===");
        int[] numbers = { 1, 12, 124, 7, 18, 22 };
        int even = 0, odd = 0;

        for (int n : numbers) {
            if (n % 2 == 0) even++;
            else             odd++;
        }

        System.out.println("Even: " + even + ", Odd: " + odd);
    }
}
