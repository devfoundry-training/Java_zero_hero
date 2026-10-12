package com.teaching.testing;

/**
 * A simple calculator - the subject under test for the testing domain.
 *
 * Deliberately kept small: the point is to demonstrate JUnit 5 patterns,
 * not to implement a full-featured calculator.
 *
 * Three interesting cases for testing:
 * - Happy paths (normal inputs)
 * - Edge cases (zero, negative, boundary values)
 * - Exception paths (divide by zero, negative factorial)
 */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * @throws ArithmeticException if divisor is zero
     */
    public double divide(double dividend, double divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return dividend / divisor;
    }

    /**
     * Recursive factorial.
     *
     * @param n must be >= 0
     * @throws IllegalArgumentException if n < 0
     */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0, got: " + n);
        }
        if (n == 0 || n == 1) return 1L;
        return (long) n * factorial(n - 1);
    }
}
