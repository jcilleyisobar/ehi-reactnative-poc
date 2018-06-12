package io.dwak.reactor.unbinder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this to have Unbinder.unbind unbind the annotated field
 */
@Target(ElementType.FIELD)
public @interface AutoUnbind {
}
