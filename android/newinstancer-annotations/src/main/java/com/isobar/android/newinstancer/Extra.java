package com.isobar.android.newinstancer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this for generation of Fragment Builders
 * Annotate public static final String fields to generate builder methods to be used instead of the {@code newInstance} pattern
 */
@Target(ElementType.FIELD)
public @interface Extra {
    /**
     * Class type of the extra
     * @return Class type of the extra
     */
    Class value();

    Class type() default void.class;

    /**
     * Name to use as the builder method, if not used, defaults to camel-casing the annotated extra-key
     * @return String to use as method name
     */
    String name() default "";

    /**
     * Whether or not the extra is required for fragment instantiation
     * If a required Extra is not added via the builder, the generated builder will throw an IllegalStateException
     * @return true if the extra is required
     */
    boolean required() default true;

    boolean large() default false;
}
