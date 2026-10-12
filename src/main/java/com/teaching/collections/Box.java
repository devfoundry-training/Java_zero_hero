package com.teaching.collections;

/**
 * Canonical generic container - holds one value of type T.
 *
 * This is the single source-of-truth for Box<T> in this project.
 * The original source repository contained a second inner Box<T> inside
 * GenericsDemo.java. That duplication is removed here; GenericsRunner uses
 * this class to demonstrate both safe and unsafe (raw-type) usage.
 *
 * Generic type parameter T:
 * - Declared in angle brackets on the class: class Box<T>
 * - T is a placeholder replaced by the caller's actual type at compile time:
 *       Box<String>  -> compiler enforces that only Strings go in and come out
 *       Box<Integer> -> compiler enforces Integer
 *       Box (raw)    -> no enforcement; pre-Java-5 style; unsafe - see GenericsRunner
 */
public class Box<T> {

    private T value;

    public Box(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Box[" + value + "]";
    }
}
