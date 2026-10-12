package com.teaching.iteration;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates for, enhanced-for, while, do-while, and loop control (break / continue / labelled break).
 *
 * Original IterationDemo.java placed all demos in a single main() and used a bare return
 * inside a nested loop to demonstrate early exit from main - a confusing teaching pattern
 * that also silently skipped code below it. Replaced with:
 * - named static methods for each concept (easier to navigate)
 * - a labelled break to show structured nested-loop exit without exiting main()
 */
public class LoopsRunner {

    public static void main(String[] args) {
        indexedFor();
        enhancedFor();
        whileLoop();
        doWhileLoop();
        twoDimensionalArray();
        listIteration();
        loopControl();
    }

    static void indexedFor() {
        System.out.println("=== Indexed for ===");
        int[] numbers = { 10, 20, 30, 40, 50 };
        for (int i = 0; i < numbers.length; i++) {
            System.out.println("index " + i + " -> " + numbers[i]);
        }
    }

    static void enhancedFor() {
        System.out.println("\n=== Enhanced for (for-each) ===");
        int[] numbers = { 10, 20, 30, 40, 50 };
        // Use enhanced-for when you don't need the index. Cleaner and less error-prone.
        for (int n : numbers) {
            System.out.print(n + " ");
        }
        System.out.println();
    }

    static void whileLoop() {
        System.out.println("\n=== while ===");
        int count = 0;
        while (count < 5) {
            System.out.print(count + " ");
            count++;
        }
        System.out.println();
    }

    static void doWhileLoop() {
        System.out.println("\n=== do-while (always runs at least once) ===");
        int count = 10;  // already past the condition
        do {
            System.out.println("Ran with count=" + count);
            count++;
        } while (count < 5);  // false immediately, but the body ran once
    }

    static void twoDimensionalArray() {
        System.out.println("\n=== 2D array traversal ===");
        int[][] matrix = {
            { 1, 2, 3 },
            { 4, 5, 6 },
            { 7, 8, 9 }
        };
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.print(matrix[row][col] + " ");
            }
            System.out.println();
        }
    }

    static void listIteration() {
        System.out.println("\n=== List iteration ===");
        List<String> fruit = new ArrayList<>(List.of("Banana", "Apple", "Orange", "Pear"));
        fruit.remove("Pear");
        for (String f : fruit) {
            System.out.println(f);
        }
    }

    static void loopControl() {
        System.out.println("\n=== break ===");
        for (int i = 0; i < 10; i++) {
            if (i == 5) break;       // exits the loop immediately
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.println("\n=== continue ===");
        for (int i = 0; i < 6; i++) {
            if (i == 3) continue;    // skips rest of this iteration; loop continues
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.println("\n=== labelled break (exits a specific outer loop) ===");
        outer:
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (j == 2) break outer;  // exits the outer loop, not just the inner
                System.out.println("i=" + i + ", j=" + j);
            }
        }
    }
}
