package com.teaching.oop;

/**
 * Subclass of Vehicle. Demonstrates:
 * - calling super() to invoke the parent constructor
 * - adding subclass-specific fields (make, model, noOfDoors)
 * - overriding toString()
 * - constructor overloading within an inheritance hierarchy
 */
public class Car extends Vehicle {

    private String make;
    private String model;
    private int noOfDoors;

    // No-arg: calls Vehicle() -> Vehicle(4)
    public Car() {
        super();
        this.noOfDoors = 4;
    }

    public Car(String make, String model) {
        this(make, model, 4);   // delegate to the full constructor
    }

    // Canonical constructor
    public Car(String make, String model, int noOfDoors) {
        super(4);               // cars have 4 wheels
        this.make = make;
        this.model = model;
        this.noOfDoors = noOfDoors;
    }

    @Override
    public void describe() {
        super.describe();       // prints "Car with 4 wheels"
        System.out.println("  -> " + this);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%d-door)", make, model, noOfDoors);
    }

    public String getMake()    { return make; }
    public String getModel()   { return model; }
    public int getNoOfDoors()  { return noOfDoors; }
}
