package com.teaching.functions;

import java.util.Scanner;

/**
 * A simple service class that wraps Scanner to provide typed, prompted input.
 * Demonstrates encapsulation: the Scanner is private; callers use clean typed methods.
 *
 * Fixes applied from source repo:
 *   1. closeScanner() printed "closing scanner.." but never called scanner.close() - resource leak.
 *      Renamed to close() and the Scanner is now actually closed.
 *   2. The catch block for NumberFormatException was empty (silently swallowed the error).
 *      Now logs the message so the caller can see what went wrong.
 */
public class ScannerService {

    private final Scanner scanner;

    public ScannerService() {
        scanner = new Scanner(System.in);
    }

    public String getString(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine();
    }

    public int getInt(String prompt) {
        System.out.print(prompt + " ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid integer: " + e.getMessage());
            return 0;
        }
    }

    public double getDouble(String prompt) {
        System.out.print(prompt + " ");
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number: " + e.getMessage());
            return 0.0;
        }
    }

    // Implements AutoCloseable pattern - can be used in try-with-resources
    public void close() {
        scanner.close();
    }
}
