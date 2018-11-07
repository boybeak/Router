package com.github.boybeak.router.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Intent {
    Class activity() default void.class;
    String action() default "";
    int[] flags() default {};
    String[] categories() default {};
    String packageName() default "";
    String type() default "";
    boolean normalized() default false;
    boolean forResult() default false;
    int requestCode() default 0;
    Class[] interceptors() default {};
}
