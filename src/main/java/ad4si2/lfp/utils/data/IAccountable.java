package ad4si2.lfp.utils.data;

import ad4si2.lfp.data.entities.account.Account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

public interface IAccountable {

    @Nullable
    Long getAccountId();

    void setAccountId(@Nonnull final Long accountId);

    @Nullable
    Account getAccount();

    void setAccount(@Nullable final Account account);

    @Nonnull
    Date getD();

    void setD(@Nonnull final Date d);
}
