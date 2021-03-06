package net.engin33r.luaspigot.lua.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Annotation for quick registration of library functions.
 */
@Target(value=METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LibraryFunctionDefinition {
    String value() default "";
    String module() default "";
}
