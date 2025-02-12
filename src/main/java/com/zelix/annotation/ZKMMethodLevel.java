package com.zelix.annotation;

import java.lang.annotation.*;

/**
 * Used to override certain obfuscation settings at the method level
 */
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD})
public @interface ZKMMethodLevel {
    FlowObfuscationPolicy   obfuscateFlow() default FlowObfuscationPolicy.LIGHT;
    ExceptionObfuscationPolicy exceptionObfuscation() default ExceptionObfuscationPolicy.LIGHT;
}