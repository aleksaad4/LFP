package ad4si2.lfp.utils.jpa;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Author:      doa <br>
 * Date:        14.09.11, 13:25 <br>
 * Company:     SofIT labs <br>
 * Revision:    $ <br>
 * Description: <br>
 */
public abstract class CustomCollectionType<C extends Collection<T>, T> implements UserType {

    private static final int[] SQL_TYPES = {Types.VARCHAR};

    protected abstract C collectionInstance();

    public abstract T deserialize(@Nullable final String value) throws SQLException;

    public String serialize(@Nonnull final T value) throws SQLException {
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
        final C collection = collectionInstance();
        if (value != null) {
            final StringTokenizer tokenizer = new StringTokenizer(value, getDelim());
            while (tokenizer.hasMoreTokens()) {
                collection.add(deserialize(tokenizer.nextToken()));
            }
        }
        return collection;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            final StringBuilder buff = new StringBuilder();

            final Iterator<T> it = ((Collection<T>) value).iterator();
            while (it.hasNext()) {
                buff.append(serialize(it.next()));
                if (it.hasNext()) {
                    buff.append(getDelim());
                }
            }
            st.setString(index, buff.toString());
        }
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        if (value != null) {
            final C collection = collectionInstance();
            collection.addAll((Collection<T>) value);
            return collection;
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

    protected String getDelim() {
        return ",";
    }
}