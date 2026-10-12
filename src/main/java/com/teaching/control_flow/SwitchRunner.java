package com.teaching.control_flow;

/**
 * Demonstrates traditional switch statements and modern switch expressions (Java 14+).
 *
 * Original ConditionsExtended.java was an instance method that opened a Scanner
 * internally and never closed it - a resource leak. It also interleaved Scanner
 * input with switch logic, obscuring the switch teaching point.
 * Fixed by: converting to static methods that accept parameters, removing Scanner.
 */
public class SwitchRunner {

    public static void main(String[] args) {
        traditionalSwitch(2);
        traditionalSwitch(9);   // hits default

        modernSwitchExpression(1);
        modernSwitchExpression(9);  // hits default

        switchWithMultipleLabels(6);
        switchWithMultipleLabels(2);
    }

    /**
     * Traditional switch: requires explicit break to stop fall-through.
     * Forgetting break is one of the most common Java bugs in older code.
     */
    static void traditionalSwitch(int day) {
        System.out.println("\n--- Traditional switch (day=" + day + ") ---");
        String name;
        switch (day) {
            case 1:
                name = "Monday";
                break;
            case 2:
                name = "Tuesday";
                break;
            case 3:
                name = "Wednesday";
                break;
            default:
                name = "Unknown - enter 1 to 3";
                break;
        }
        System.out.println("Day: " + name);
    }

    /**
     * Modern switch expression (Java 14+):
     * - arrow (->) replaces colon + break; no fall-through possible
     * - switch is an expression: it produces a value assigned to a variable
     * - compiler enforces exhaustiveness; a missing case is a compile error
     */
    static void modernSwitchExpression(int day) {
        System.out.println("\n--- Modern switch expression (day=" + day + ") ---");
        String name = switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            default -> "Unknown - enter 1 to 3";
        };
        System.out.println("Day: " + name);
    }

    /**
     * Multiple labels on one case - supported in both styles.
     * Here shown with the modern arrow form.
     */
    static void switchWithMultipleLabels(int day) {
        System.out.println("\n--- Multiple labels (day=" + day + ") ---");
        String type = switch (day) {
            case 1, 2, 3, 4, 5 -> "Weekday";
            case 6, 7           -> "Weekend";
            default             -> "Invalid";
        };
        System.out.println(type);
    }
}
