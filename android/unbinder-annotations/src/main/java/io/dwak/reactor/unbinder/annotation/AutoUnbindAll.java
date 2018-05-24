package io.dwak.reactor.unbinder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this annotation to unbind every ReactorVar in the class
 */
@Target(ElementType.TYPE)
public @interface AutoUnbindAll {
}
