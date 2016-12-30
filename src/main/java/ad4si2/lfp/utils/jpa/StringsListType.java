package ad4si2.lfp.utils.jpa;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;

/**
 * Author:      nik <br>
 * Date:        04.07.11, 17:37 <br>
 * Company:     SofIT labs <br>
 * Revision:    $ <br>
 * Description: <br>
 */
public class StringsListType extends CustomListType<String> {
    /**
     * RCS (CVS/SVN) information
     *
     * @noinspection UnusedDeclaration
     */
    private static final String RCS_ID = "$Id: StringsListType.java 11428 2011-08-25 08:32:27Z ais $";

    private static final String ESCAPED_DELIM = "-COMMA-";

    @Override
    public String deserialize(@Nullable final String value) throws SQLException {
        return value.replace(ESCAPED_DELIM, getDelim());
    }

    @Override
    public String serialize(@Nonnull final String value) throws SQLException {
        return value.replace(getDelim(), ESCAPED_DELIM);
    }
}
