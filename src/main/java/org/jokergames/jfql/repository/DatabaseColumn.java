package org.jokergames.jfql.repository;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseColumn {

    String name() default "%";

    boolean primary() default false;

    boolean json() default false;

}
