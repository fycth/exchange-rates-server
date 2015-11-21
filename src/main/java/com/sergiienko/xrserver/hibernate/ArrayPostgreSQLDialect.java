package com.sergiienko.xrserver.hibernate;

import java.sql.Types;
import org.hibernate.dialect.PostgreSQL9Dialect;

/**
 * PostgreSQL custom dialect for enabling integer[] type
 */
public class ArrayPostgreSQLDialect extends PostgreSQL9Dialect {
    /**
     * Integer[] implementation
     */
    public ArrayPostgreSQLDialect() {
        super();
        /* For other type array you can change integer[] to that array type */
        this.registerColumnType(Types.ARRAY, "integer[]");
    }

}