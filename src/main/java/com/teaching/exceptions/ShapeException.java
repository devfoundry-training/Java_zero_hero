package com.teaching.exceptions;

/**
 * Root of the shape exception hierarchy.
 * Extends Exception (checked) - callers must handle or declare it.
 * Two constructors: message-only, and message + cause (wrapping another exception).
 *
 * Full exception demos (try-catch, LBYL vs EAFP, try-with-resources) live in ExceptionRunner.
 * These classes are placed here early because the oop module (Rectangle) uses them.
 */
public class ShapeException extends Exception {

    public ShapeException(String message) {
        super(message);
    }

    public ShapeException(String message, Throwable cause) {
        super(message, cause);
    }
}
