package ad4si2.lfp.utils.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

public interface IAccountable {

    @Nullable
    Long getAccountId();

    void setAccountId(@Nonnull Long accountId);

    @Nonnull
    Date getD();

    void setD(@Nonnull final Date d);
}
