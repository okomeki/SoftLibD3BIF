package net.siisise.d3bif.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * table(column) で
 */
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ForignKey {

    String value();
}
