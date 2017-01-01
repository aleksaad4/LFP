package ad4si2.lfp.data.services.account;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.repositories.account.AccountRepository;
import ad4si2.lfp.utils.data.IAccountCRUDService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AccountService extends IAccountCRUDService<Account, Long, AccountRepository> {

    @Nullable
    Account findByLoginAndPasswordAndDeletedFalse(@Nonnull final String login, @Nonnull final String password);
}
