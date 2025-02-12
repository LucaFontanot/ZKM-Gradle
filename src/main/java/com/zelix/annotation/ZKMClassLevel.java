package com.zelix.annotation;

import java.lang.annotation.*;

/**
 * Used to override certain obfuscation settings at the class level
 */
@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.TYPE})
public @interface ZKMClassLevel {
    FlowObfuscationPolicy   obfuscateFlow() default FlowObfuscationPolicy.LIGHT;
    ExceptionObfuscationPolicy exceptionObfuscation() default ExceptionObfuscationPolicy.LIGHT;
}