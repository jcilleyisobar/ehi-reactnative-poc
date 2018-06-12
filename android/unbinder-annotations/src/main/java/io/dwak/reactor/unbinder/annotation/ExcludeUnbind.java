package io.dwak.reactor.unbinder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this to exclude a field from being automatically unbound inside of a class annotated with {@link AutoUnbindAll}
 */
@Target(ElementType.FIELD)
public @interface ExcludeUnbind {
}
