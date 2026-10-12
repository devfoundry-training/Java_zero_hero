package com.teaching.functions;

/**
 * Demonstrates constructor chaining using this(...).
 * Every constructor delegates to the most complete one - avoids duplicating initialisation logic.
 *
 * Fix applied: removed 'import javax.swing.plaf.synth.SynthOptionPaneUI' - a dead import
 * from a Swing UI library with no relevance to this class.
 *
 * Also removed 'public int count' that was incremented in the constructor.
 * As an instance field it counted nothing useful (each object has its own copy starting at 0,
 * incremented to 1, then never read). A static counter would track instance count;
 * that pattern is covered in the static fields module.
 */
public class Aeroplane {

    private final boolean hasWings;
    private final String make;

    // No-arg: delegates to (boolean) -> delegates to (boolean, String)
    public Aeroplane() {
        this(true);
    }

    // One-arg: delegates to the full constructor
    public Aeroplane(boolean hasWings) {
        this(hasWings, "Airbus");
    }

    // Canonical constructor - all fields assigned here
    public Aeroplane(boolean hasWings, String make) {
        this.hasWings = hasWings;
        this.make = make;
    }

    public void fly() {
        System.out.println(make + (hasWings ? " is flying!" : " has no wings - cannot fly."));
    }

    @Override
    public String toString() {
        return String.format("Aeroplane[make=%s, hasWings=%b]", make, hasWings);
    }
}
