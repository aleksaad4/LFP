package ru.sofitlabs.chat.common.utils.jpa;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Author:      doa <br>
 * Date:        14.09.11, 13:25 <br>
 * Company:     SofIT labs <br>
 * Revision:    $ <br>
 * Description: <br>
 */
public abstract class CustomMapType<K, V> implements UserType {

    private static final int[] SQL_TYPES = {Types.VARCHAR};

    public abstract K deserializeKey(@Nullable final String key) throws SQLException;

    public abstract V deserializeValue(@Nullable final String value) throws SQLException;

    public String serializeKey(@Nonnull final K key) throws SQLException {
        return key.toString();
    }

    public String serializeValue(@Nonnull final V value) throws SQLException {
        return value.toString();
    }

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Class returnedClass() {
        return List.class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return x == y || (x != null && y != null && x.equals(y));
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session, final Object owner) throws HibernateException, SQLException {
        final String value = rs.getString(names[0]);
        if (value == null) {
            return new HashMap<K, V>();
        } else {
            final HashMap<K, V> map = new HashMap<>();

            final StringTokenizer tokenizer = new StringTokenizer(value, getEntryDelim());
            while (tokenizer.hasMoreTokens()) {
                final String keyValToken = tokenizer.nextToken();
                map.put(deserializeKey(keyValToken.split(getKeyValueDelim())[0]),
                        deserializeValue(keyValToken.split(getKeyValueDelim())[1]));
            }

            return map;
        }
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            final StringBuilder buff = new StringBuilder();

            final Iterator<Map.Entry<K, V>> it = ((Map<K, V>) value).entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<K, V> entry = it.next();
                buff.append(serializeKey(entry.getKey()));
                buff.append(getKeyValueDelim());
                buff.append(serializeValue(entry.getValue()));
                if (it.hasNext()) {
                    buff.append(getEntryDelim());
                }
            }

            st.setString(index, buff.toString());
        }
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value != null) {
            return new HashMap((Map) value);
        } else {
            return null;
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }

    protected String getEntryDelim() {
        return ",";
    }

    protected String getKeyValueDelim() {
        return "->";
    }
}