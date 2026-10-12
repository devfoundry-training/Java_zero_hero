package com.teaching.oop;

/**
 * Base class for the Vehicle hierarchy.
 * Demonstrates inheritance: Car extends Vehicle and inherits noOfWheels and describe().
 *
 * Fix applied from source repo:
 *   The original only had Vehicle(int noOfWheels). Car() called super() which
 *   resolved to the no-arg constructor - which did not exist - causing a compile error.
 *   Fixed by adding a no-arg constructor that delegates to the parameterised one.
 */
public class Vehicle {

    protected int noOfWheels;

    // No-arg delegates to the parameterised constructor with a sensible default
    public Vehicle() {
        this(4);
    }

    public Vehicle(int noOfWheels) {
        this.noOfWheels = noOfWheels;
    }

    public void describe() {
        System.out.println(getClass().getSimpleName() + " with " + noOfWheels + " wheels");
    }
}
