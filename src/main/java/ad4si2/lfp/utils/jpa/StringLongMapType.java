package ru.sofitlabs.chat.common.utils.jpa;

import javax.annotation.Nullable;
import java.sql.SQLException;

/**
 * Author:      daa
 * Date:        14.11.16
 * Company:     SofIT labs
 */
public class StringLongMapType extends CustomMapType<String, Long> {

    @Override
    public String deserializeKey(@Nullable final String key) throws SQLException {
        return key;
    }

    @Override
    public Long deserializeValue(@Nullable final String value) throws SQLException {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            throw new SQLException("Incorrect map long value [" + value + "]");
        }
    }
}
