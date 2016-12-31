package ad4si2.lfp.data.repositories.account;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface AccountRepository extends RepositoryWithDeleted<Account, Long> {

    @Nonnull
    List<Account> findByLoginAndPasswordAndDeletedFalse(@Nonnull final String login, @Nonnull final String password);

    @Nonnull
    List<Account> findByLoginAndDeletedFalse(@Nonnull final String login);

    @Nonnull
    List<Account> findByEmailAndDeletedFalse(@Nonnull final String email);
}
