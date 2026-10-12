package com.teaching.exceptions;

/**
 * Thrown when a square is constructed with sides that are not equal.
 * Shows a three-level hierarchy: ShapeException -> InvalidDimensionException -> InvalidSquareException.
 * Catching any parent type in the hierarchy will also catch this.
 */
public class InvalidSquareException extends InvalidDimensionException {

    public InvalidSquareException(String message) {
        super(message);
    }
}
