package ad4si2.lfp.utils.jpa;

import javax.annotation.Nullable;
import java.sql.SQLException;

/**
 * Author:      doa <br>
 * Date:        14.09.11, 13:25 <br>
 * Company:     SofIT labs <br>
 * Revision:    $ <br>
 * Description: <br>
 */
public class LongsListType extends CustomListType<Long> {

    @Override
    public Long deserialize(@Nullable final String value) throws SQLException {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            throw new SQLException("Incorrect list of longs value [" + value + "]");
        }
    }
}