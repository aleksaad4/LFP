package ad4si2.lfp.data.services.account;

import ad4si2.lfp.BaseTest;
import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.AccountRole;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;

public class AccountServiceTest extends BaseTest {

    @Inject
    private AccountService accountService;

    @Test
    public void testValidation() {
        final Account account = accountService.create(new Account("login", "password", AccountRole.ADMIN));

        // нельзя создать с таким же логином
        Assert.assertTrue(accountService.validateEntry(new Account("login", "password", AccountRole.ADMIN), false).hasErrors());
        // подаём на редактирование другую запись с таким же логином
        Assert.assertTrue(accountService.validateEntry(new Account("login", "password", AccountRole.ADMIN), true).hasErrors());
        // обновление этого объекта
        Assert.assertFalse(accountService.validateEntry(account, true).hasErrors());
    }
}