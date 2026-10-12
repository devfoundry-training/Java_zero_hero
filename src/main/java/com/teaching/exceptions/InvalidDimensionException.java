package com.teaching.exceptions;

/**
 * Thrown when a shape is given a dimension that is zero or negative.
 * Extends ShapeException - callers catching ShapeException also catch this.
 */
public class InvalidDimensionException extends ShapeException {

    public InvalidDimensionException(String message) {
        super(message);
    }
}
