package com.teaching.basics;

/**
 * Enum with associated data - each constant carries an integer code.
 * Used by EnumRunner to demonstrate switch expressions and reference semantics.
 */
public enum Status {

    NEW(10),
    IN_PROGRESS(20),
    COMPLETE(30);

    private final int code;  // enums can hold fields, exactly like a class

    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
