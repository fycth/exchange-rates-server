package com.sergiienko.xrserver.hibernate;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

/**
 * Hibernate does not support 'list of integers' field for PostgreSQL
 * This is an implementation for the integer[] type that works for PostgreSQL and Hibernate
 */
public class IntegerArrayType implements UserType {

    @Override
    public final Object assemble(final Serializable cached, final Object owner)
            throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public final Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    @Override
    public final Serializable disassemble(final Object value) throws HibernateException {
        return (Integer[]) this.deepCopy(value);
    }

    @Override
    public final boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public final int hashCode(final Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public final boolean isMutable() {
        return true;
    }

    @Override
    public final Object nullSafeGet(final ResultSet resultSet, final String[] names,
                              final SessionImplementor session, final Object owner)
            throws HibernateException, SQLException {
        Array array = resultSet.getArray(names[0]);
        Integer[] javaArray = (Integer[]) array.getArray();
        return javaArray;
    }

    @Override
    public final void nullSafeSet(final PreparedStatement statement, final Object value,
                            final int index, final SessionImplementor session) throws HibernateException,
            SQLException {
        Connection connection = statement.getConnection();
        Integer[] castObject = (Integer[]) value;
        Array array = connection.createArrayOf("integer", castObject);
        statement.setArray(index, array);
    }

    @Override
    public final Object replace(final Object original, final Object target, final Object owner)
            throws HibernateException {
        return original;
    }

    @Override
    public final Class<Integer[]> returnedClass() {
        return Integer[].class;
    }

    @Override
    public final int[] sqlTypes() {
        return new int[] {Types.ARRAY};
    }
}