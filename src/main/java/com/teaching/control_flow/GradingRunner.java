package com.teaching.control_flow;

import java.util.Scanner;

/**
 * Applied control-flow example: a grade calculator combining input validation,
 * if/else logic, and a traditional switch statement.
 *
 * Fixes applied from TaskTwoExtension.java:
 *   1. scanner.nextInt() leaves a newline in the buffer, causing subsequent nextLine()
 *      calls to read an empty string. Fixed by using Integer.parseInt(scanner.nextLine()).
 *   2. Typo "Distiction" corrected to "Distinction".
 *   3. Scanner now closed in a finally block to guarantee closure on any code path.
 *   4. Grade logic extracted to a named method (toGrade) - easier to test and read.
 */
public class GradingRunner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("=== Grading Application ===");
            System.out.print("Enter your score (1-100): ");
            int score = Integer.parseInt(scanner.nextLine());

            if (score < 1 || score > 100) {
                System.out.println("Score must be between 1 and 100.");
            } else {
                printResult(toGrade(score));
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a whole number.");
        } finally {
            scanner.close();  // always runs, even if an exception was thrown
        }
    }

    static String toGrade(int score) {
        if (score < 50)       return "fail";
        else if (score <= 60) return "C";
        else if (score <= 70) return "B";
        else                  return "A";
    }

    static void printResult(String grade) {
        // Traditional switch used here deliberately to contrast with SwitchRunner,
        // which shows the modern arrow-style switch expression.
        switch (grade) {
            case "fail": System.out.println("Result: Failed");      break;
            case "C":    System.out.println("Result: Pass");        break;
            case "B":    System.out.println("Result: Merit");       break;
            case "A":    System.out.println("Result: Distinction"); break;
            default:     System.out.println("Unknown grade.");
        }
    }
}
