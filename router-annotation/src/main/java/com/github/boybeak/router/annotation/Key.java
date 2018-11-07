package com.github.boybeak.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
public @interface Key {
    String value() default "";
    ExtraType extraType() default ExtraType.BASIC_OR_ARRAY;
}
