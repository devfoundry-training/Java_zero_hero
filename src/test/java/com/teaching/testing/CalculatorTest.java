package com.teaching.testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 test suite for Calculator.
 *
 * Key annotations demonstrated:
 *
 *   @Test - marks a method as a test case
 *   @DisplayName - human-readable label shown in test reports and IDE
 *   @BeforeEach - runs before every @Test method; used to reset shared state
 *   @AfterEach - runs after every @Test method; used for cleanup / logging
 *   @ParameterizedTest - runs the same test logic with multiple inputs
 *   @ValueSource - supplies a single series of values to a parameterised test
 *   @CsvSource - supplies rows of comma-separated values (multiple params)
 *
 * Key assertion methods (from org.junit.jupiter.api.Assertions):
 *
 *   assertEquals(expected, actual) - fails if values differ
 *   assertTrue(condition) - fails if condition is false
 *   assertThrows(ExceptionType, lambda) - fails if the lambda does NOT throw the expected type;
 *                                          returns the thrown exception for further inspection
 *
 * Naming convention: methodUnderTest_Scenario_ExpectedBehaviour
 *   e.g. divide_ByZero_ThrowsArithmeticException
 *   This makes failure messages self-explanatory without reading the test body.
 */
@DisplayName("Calculator")
class CalculatorTest {

    // ---- Test fixture ----

    Calculator calc;   // fresh instance for each test (see setUp)

    /**
     * Runs before EVERY @Test method.
     * Creating a new Calculator here ensures tests do not share mutable state.
     */
    @BeforeEach
    void setUp() {
        calc = new Calculator();
    }

    /**
     * Runs after EVERY @Test method.
     * Useful for releasing resources (files, connections, mocks).
     * Printing here shows the lifecycle - remove in real projects.
     */
    @AfterEach
    void tearDown() {
        // Nothing to release here; the output just visualises the lifecycle.
        System.out.println("    [@AfterEach] test finished");
    }

    // =========================================================================
    // add()
    // =========================================================================

    @Test
    @DisplayName("add: two positive integers")
    void add_TwoPositives_ReturnsSum() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    @DisplayName("add: positive and negative integers")
    void add_PositiveAndNegative_ReturnsCorrectSum() {
        assertEquals(-1, calc.add(-3, 2));
    }

    @Test
    @DisplayName("add: identity - adding zero returns the same number")
    void add_Zero_ReturnsUnchangedValue() {
        assertEquals(42, calc.add(42, 0));
    }

    /**
     * @ParameterizedTest + @CsvSource: the same test logic runs once per CSV row.
     * Each row is parsed into the method parameters (a, b, expected).
     * The name attribute customises the display in the test report.
     */
    @ParameterizedTest(name = "add({0}, {1}) = {2}")
    @CsvSource({
        "0,   0,   0",
        "1,   1,   2",
        "-5,  5,   0",
        "100, -50, 50",
        "-10, -10, -20"
    })
    @DisplayName("add: parameterised cases")
    void add_VariousInputs_ReturnsExpected(int a, int b, int expected) {
        assertEquals(expected, calc.add(a, b));
    }

    // =========================================================================
    // subtract()
    // =========================================================================

    @Test
    @DisplayName("subtract: basic case")
    void subtract_BasicCase_ReturnsDifference() {
        assertEquals(3, calc.subtract(8, 5));
    }

    @Test
    @DisplayName("subtract: result is negative")
    void subtract_SmallerFromLarger_ReturnsNegative() {
        assertEquals(-3, calc.subtract(5, 8));
    }

    // =========================================================================
    // multiply()
    // =========================================================================

    @Test
    @DisplayName("multiply: two positives")
    void multiply_TwoPositives_ReturnsProduct() {
        assertEquals(12, calc.multiply(3, 4));
    }

    @Test
    @DisplayName("multiply: by zero always yields zero")
    void multiply_ByZero_ReturnsZero() {
        assertEquals(0, calc.multiply(999, 0));
    }

    /**
     * @ValueSource supplies a single array of values - each value is passed as the
     * sole argument to the test method. Useful when the expected result is the same
     * for all inputs (here: any number x 1 = itself).
     */
    @ParameterizedTest(name = "multiply({0}, 1) = {0}")
    @ValueSource(ints = {-100, -1, 0, 1, 42, 1_000_000})
    @DisplayName("multiply: identity - n x 1 = n")
    void multiply_ByOne_ReturnsSameNumber(int n) {
        assertEquals(n, calc.multiply(n, 1));
    }

    // =========================================================================
    // divide()
    // =========================================================================

    @Test
    @DisplayName("divide: exact result")
    void divide_ExactDivision_ReturnsQuotient() {
        assertEquals(4.0, calc.divide(8, 2));
    }

    @Test
    @DisplayName("divide: non-integer result")
    void divide_NonIntegerResult_ReturnsDouble() {
        assertEquals(2.5, calc.divide(5, 2));
    }

    /**
     * assertThrows: verifies that the supplied lambda throws the expected exception type.
     * Returns the thrown exception so you can assert on its message or cause.
     * The test FAILS if no exception is thrown (or if a different type is thrown).
     */
    @Test
    @DisplayName("divide: by zero throws ArithmeticException")
    void divide_ByZero_ThrowsArithmeticException() {
        ArithmeticException ex = assertThrows(
            ArithmeticException.class,
            () -> calc.divide(10, 0)   // lambda must throw for the test to pass
        );
        assertTrue(ex.getMessage().contains("zero"),
            "Exception message should mention 'zero'; was: " + ex.getMessage());
    }

    // =========================================================================
    // factorial()
    // =========================================================================

    @ParameterizedTest(name = "factorial({0}) = {1}")
    @CsvSource({
        "0,  1",
        "1,  1",
        "2,  2",
        "5,  120",
        "10, 3628800"
    })
    @DisplayName("factorial: known values")
    void factorial_KnownValues_ReturnsExpected(int n, long expected) {
        assertEquals(expected, calc.factorial(n));
    }

    @Test
    @DisplayName("factorial: negative input throws IllegalArgumentException")
    void factorial_NegativeInput_ThrowsIllegalArgumentException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> calc.factorial(-1)
        );
    }
}
