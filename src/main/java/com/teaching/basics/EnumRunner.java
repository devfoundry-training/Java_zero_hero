package com.teaching.basics;

/**
 * Demonstrates enum types: switch expressions, pass-by-value for enum variables,
 * and pass-by-reference for objects that contain an enum field.
 *
 * This demo reveals a subtle distinction live instructors often demonstrate:
 * reassigning an enum local variable has no effect on the caller,
 * but mutating a field inside an object does.
 */
public class EnumRunner {

    public static void main(String[] args) {
        Status current = Status.NEW;
        System.out.println("Status : " + current);
        System.out.println("Code   : " + current.getCode());

        // Switch expression (Java 14+): exhaustive, no fall-through, returns a value.
        // The compiler will error if a case is missing - unlike traditional switch.
        String description = switch (current) {
            case NEW         -> "Work has just started";
            case IN_PROGRESS -> "Work is ongoing";
            case COMPLETE    -> "Work is finished";
        };
        System.out.println("Info   : " + description);

        // --- Pass-by-value: reassigning an enum variable ---
        System.out.println("\n--- Enum variable (pass-by-value) ---");
        System.out.println("Before : " + current);
        changeStatus(current);
        System.out.println("After  : " + current);  // still NEW - the caller's variable is unchanged

        // --- Pass-by-reference: mutating a field on an object ---
        System.out.println("\n--- Object containing an enum (pass-by-reference) ---");
        Task task = new Task(Status.NEW);
        System.out.println("Before : " + task.status);
        updateTask(task);
        System.out.println("After  : " + task.status);  // COMPLETE - the object was mutated
    }

    // Enum constants are passed by value. Reassigning the local parameter
    // has no effect on the variable the caller passed in.
    static void changeStatus(Status s) {
        s = Status.COMPLETE;  // only changes the local copy; caller sees no difference
    }

    // Object references are passed by value, but the object itself is shared.
    // Changing a field on the object is visible to the caller.
    static void updateTask(Task task) {
        task.status = Status.COMPLETE;
    }

    static class Task {
        Status status;
        Task(Status status) { this.status = status; }
    }
}
