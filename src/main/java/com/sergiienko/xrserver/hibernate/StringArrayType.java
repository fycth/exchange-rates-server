package com.sergiienko.xrserver.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.Array;

/**
 * Hibernate does not support 'list of strings' field for PostgreSQL
 * This is an implementation for the String[] type that works for PostgreSQL and Hibernate
 */
public class StringArrayType implements UserType {

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
        return (String[]) this.deepCopy(value);
    }

    @Override
    public final boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == y) {
            return true;
        } else if (x == null || y == null) {
            return false;
        } else {
            return x.equals(y);
        }
    }

    @Override
    public final int hashCode(final Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public final boolean isMutable() {
        return false;
    }

    @Override
    public final Object nullSafeGet(final ResultSet resultSet, final String[] names,
                              final SessionImplementor session, final Object owner)
            throws HibernateException, SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        String[] javaArray = (String[]) resultSet.getArray(names[0]).getArray();
        return javaArray;
    }

    @Override
    public final void nullSafeSet(final PreparedStatement statement, final Object value,
                            final int index, final SessionImplementor session) throws HibernateException,
            SQLException {
        if (value == null) {
            statement.setNull(index, Types.ARRAY);
        } else {
            Connection connection = statement.getConnection();
            String[] castObject = (String[]) value;
            Array array = connection.createArrayOf("text", castObject);
            statement.setArray(index, array);
        }
    }

    @Override
    public final Object replace(final Object original, final Object target, final Object owner)
            throws HibernateException {
        return original;
    }

    @Override
    public final Class<String[]> returnedClass() {
        return String[].class;
    }

    @Override
    public final int[] sqlTypes() {
        return new int[] {Types.ARRAY};
    }
}