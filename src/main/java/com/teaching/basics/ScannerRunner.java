package com.teaching.basics;

import java.util.Scanner;

/**
 * Demonstrates reading user input from stdin via Scanner with defensive parsing.
 *
 * Note: Scanner wraps a resource (System.in). For brevity this demo closes it manually.
 * The try-with-resources pattern for auto-closing is covered in the exceptions module.
 */
public class ScannerRunner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.println("Hello, " + name + "!");

        System.out.print("Enter your height in metres (e.g. 1.75): ");
        try {
            // Always read a full line, then parse - avoids leftover newline issues
            double height = Double.parseDouble(scanner.nextLine());
            System.out.printf("Height: %.2f m%n", height);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered.");
        }

        System.out.print("Enter your age: ");
        try {
            int age = Integer.parseInt(scanner.nextLine());
            System.out.println(age >= 18 ? "Adult" : "Under 18");
        } catch (NumberFormatException e) {
            System.out.println("Age must be a whole number.");
        }

        scanner.close();
        System.out.println("Done.");
    }
}
