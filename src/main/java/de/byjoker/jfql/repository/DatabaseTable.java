package de.byjoker.jfql.repository;

import de.byjoker.jfql.util.TableType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DatabaseTable {

    String name() default "%CLASS%";

    String primary() default "%FIELDS%";

    String structure() default "%FIELDS%";

    TableType type() default TableType.RELATIONAL;

}
