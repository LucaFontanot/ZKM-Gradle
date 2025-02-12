package com.zelix.annotation;

/**
 * Indicates that the annotated program element should be excluded from obfuscation.
 */
public enum ExceptionObfuscationPolicy {
    NONE,
    LIGHT,
    HEAVY
}