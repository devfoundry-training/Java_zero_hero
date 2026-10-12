package com.teaching.oop;

/**
 * Concrete subclass of Shape. Provides getArea() using pi * r^2.
 * New class - not in the source repo. Added to give OopRunner two concrete Shape types
 * for a meaningful polymorphism demo.
 */
public class Circle extends Shape {

    private final double radius;

    public Circle(String colour, double radius) {
        super(colour);
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got: " + radius);
        }
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    public double getRadius() {
        return radius;
    }
}
