package com.zelix.annotation;

/**
 * Indicates that the annotated program element should be excluded from obfuscation.
 */
public enum FlowObfuscationPolicy {
    NONE,
    LIGHT,
    NORMAL,
    AGGRESSIVE
}