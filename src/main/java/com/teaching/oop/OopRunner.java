package com.teaching.oop;

import com.teaching.exceptions.InvalidDimensionException;
import java.util.List;

/**
 * Runnable entry point for the oop module.
 * Demonstrates: abstract classes, inheritance chains, polymorphic dispatch,
 * and instanceof with pattern matching (Java 16+).
 *
 * Runner.java (source repo) has been discarded:
 *   It declared a class named Calculator with a 'public' modifier inside a method body -
 *   illegal in Java (local classes cannot be public). The file did not compile.
 *   The polymorphism sketch it contained is correctly implemented below.
 */
public class OopRunner {

    public static void main(String[] args) throws InvalidDimensionException {
        inheritanceDemo();
        polymorphismDemo();
        instanceofPatternMatching();
    }

    // --- Inheritance ---

    static void inheritanceDemo() {
        System.out.println("=== Inheritance (Vehicle -> Car) ===");
        Vehicle v = new Vehicle(2);
        v.describe();

        Car car = new Car("Toyota", "Corolla", 4);
        car.describe();  // calls overridden describe() which also calls super.describe()
    }

    // --- Polymorphism ---

    static void polymorphismDemo() throws InvalidDimensionException {
        System.out.println("\n=== Polymorphism (Shape references) ===");

        // Reference type: Shape. Object type: Circle or Rectangle.
        // Java picks the correct describe() at RUNTIME - this is dynamic dispatch.
        List<Shape> shapes = List.of(
            new Circle("red",   5.0),
            new Rectangle("blue", 4.0, 3.0),
            new Circle("green", 2.5),
            new Rectangle("yellow", 6.0, 1.5)
        );

        for (Shape s : shapes) {
            s.describe();   // Circle.describe() or Rectangle.describe() - resolved at runtime
        }
    }

    // --- instanceof with pattern matching (Java 16+) ---

    static void instanceofPatternMatching() throws InvalidDimensionException {
        System.out.println("\n=== instanceof + pattern matching ===");
        List<Shape> shapes = List.of(
            new Circle("red", 3.0),
            new Rectangle("blue", 4.0, 5.0)
        );

        for (Shape shape : shapes) {
            // Pattern matching: check, cast, and bind to a typed variable in one expression.
            // Before Java 16 this required three separate lines.
            if (shape instanceof Circle c) {
                System.out.printf("Circle - radius=%.1f, area=%.2f%n", c.getRadius(), c.getArea());
            } else if (shape instanceof Rectangle r) {
                System.out.printf("Rect - w=%.1f, h=%.1f, area=%.2f%n",
                    r.getWidth(), r.getHeight(), r.getArea());
            }
        }
    }
}
