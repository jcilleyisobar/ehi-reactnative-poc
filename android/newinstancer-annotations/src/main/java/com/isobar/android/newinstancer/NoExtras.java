package com.isobar.android.newinstancer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this on a fragment with no args to generate a builder for it
 */
@Target(ElementType.TYPE)
public @interface NoExtras {
}
