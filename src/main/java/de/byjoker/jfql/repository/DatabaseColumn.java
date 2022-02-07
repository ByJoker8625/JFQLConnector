package de.byjoker.jfql.repository;

public @interface DatabaseColumn {

    String name() default "%VAR%";

    boolean primary() default false;

    boolean json() default false;

}
