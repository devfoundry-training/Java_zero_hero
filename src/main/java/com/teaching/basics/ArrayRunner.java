package com.teaching.basics;

/**
 * Demonstrates array declaration, initialisation, indexing, and traversal.
 * Arrays have a fixed length set at creation - use a List when the size must grow.
 */
public class ArrayRunner {

    public static void main(String[] args) {

        // Initialise with values in one step
        int[] grades = { 10, 20, 30, 40, 50 };

        // Allocate first, then populate by index
        int[] scores = new int[3];
        scores[0] = 85;
        scores[1] = 92;
        scores[2] = 78;

        System.out.println("=== int array ===");
        System.out.println("Length : " + grades.length);
        System.out.println("First  : " + grades[0]);
        System.out.println("Last   : " + grades[grades.length - 1]);   // length - 1 = last valid index

        double[] prices = { 1.55, 2.50, 3.33 };
        System.out.printf("%n=== double array ===%n");
        System.out.printf("Index 0 : %.2f%n", prices[0]);
        System.out.printf("Index 2 : %.2f%n", prices[2]);

        System.out.println("\n=== String array with enhanced-for ===");
        String[] names = { "Alice", "Bob", "Charlie" };
        for (String name : names) {
            System.out.println(name);
        }

        System.out.println("\n=== 2D array ===");
        int[][] matrix = {
            { 1, 2, 3 },
            { 4, 5, 6 },
            { 7, 8, 9 }
        };
        System.out.println("Centre cell [1][1] : " + matrix[1][1]);
        for (int[] row : matrix) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}
