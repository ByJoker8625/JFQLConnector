package org.jokergames.jfql.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DatabaseField {

    boolean formatJSON() default false;

    boolean quotationMarks() default true;

    boolean primary() default false;

    int position() default 10;

    String field();


}
