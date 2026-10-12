package com.teaching.oop;

/**
 * Abstract base class for all shapes.
 * Defines the contract (getArea) and shared behaviour (describe, getColour).
 *
 * Fixes applied from source repo:
 *   displayShape() used String.format("...%d", getArea()) where %d expects an int
 *   but getArea() returns double - throws IllegalFormatConversionException at runtime.
 *   It also used Object.class.getName() which always returns "java.lang.Object"
 *   regardless of the actual subclass. Renamed to describe() and fixed both issues.
 */
public abstract class Shape {

    protected String colour;

    public Shape(String colour) {
        this.colour = colour;
    }

    // Abstract: every concrete subclass MUST implement this.
    // Shape itself cannot implement it - it doesn't know what shape it is.
    public abstract double getArea();

    public String getColour() {
        return colour;
    }

    // this.getClass().getSimpleName() returns "Circle", "Rectangle", etc. at runtime
    public void describe() {
        System.out.printf("%-12s | colour: %-8s | area: %.2f%n",
            this.getClass().getSimpleName(), colour, getArea());
    }
}
