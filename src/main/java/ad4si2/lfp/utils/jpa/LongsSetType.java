package ru.sofitlabs.chat.common.utils.jpa;

import javax.annotation.Nullable;
import java.sql.SQLException;

/**
 * Author:      dam <br>
 * Date:        26.12.16
 */
public class LongsSetType extends CustomSetType<Long> {

    @Override
    public Long deserialize(@Nullable final String value) throws SQLException {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            throw new SQLException("Incorrect list of longs value [" + value + "]");
        }
    }
}