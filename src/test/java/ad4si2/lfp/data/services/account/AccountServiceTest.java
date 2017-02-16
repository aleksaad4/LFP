package ad4si2.lfp.data.services.account;

import ad4si2.lfp.BaseTest;
import ad4si2.lfp.TestUtils;
import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.AccountRole;
import ad4si2.lfp.data.entities.account.Admin;
import ad4si2.lfp.data.entities.account.Player;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class AccountServiceTest extends BaseTest {

    @Inject
    private AccountService accountService;

    @Nonnull
    public static Admin createAdmin(@Nonnull final String text) {
        return new Admin(text, text, text, text, text);
    }

    @Nonnull
    public static Player createPlayer(@Nonnull final String text) {
        return new Player(text, text, text, text, text);
    }

    @Test
    public void validationTest() {
        // ошибка при пустом логине
        TestUtils.validateCreateFailTest(accountService, new Admin("", "pwd"));
        // ошибка при пустом пароле
        TestUtils.validateCreateFailTest(accountService, new Admin("login", ""));

        final String adminText = "testAdmin";
        final Account account = accountService.create(createAdmin(adminText));

        // ошибка при создании аккаунта с таким же логином
        TestUtils.validateDuplicateTest(accountService, account, createAdmin(adminText));

        // нельзя обновлять тип уже существующего аккаунта
        account.setRole(AccountRole.PLAYER);
        TestUtils.validateUpdateFailTest(accountService, account);
    }

    @Test
    public void serviceTest() {
        // создание админа
        final String adminText = "testAdmin";
        final Account admin = accountService.create(createAdmin(adminText));
        Assert.assertEquals(admin.getRole(), AccountRole.ADMIN);
        Assert.assertTrue(admin instanceof Admin);

        // создание игрока
        final String playerText = "testPlayer";
        final Account player = accountService.create(createPlayer(playerText));
        Assert.assertEquals(player.getRole(), AccountRole.PLAYER);
        Assert.assertTrue(player instanceof Player);

        // создадим игрока и удалим его
        final Account player2 = accountService.create(createPlayer("testPlayer2"));
        accountService.delete(player2);

        // создадим игрока с таким же именем и заблокируем его
        final Account player3 = accountService.create(createPlayer("testPlayer2"));
        player3.setBlocked(true);
        accountService.update(player3);

        // методы для поиска
        // незаблокированных и не удаленных с логином 'testPlayer2' нет
        Assert.assertNull(accountService.findActiveByLoginAndPassword("testPlayer2", "testPlayer2"));
        // а вот с логином 'testPlayer' есть
        Assert.assertNotNull(accountService.findActiveByLoginAndPassword(playerText, playerText));
        // два не удаленных игрока на данный момент
        Assert.assertEquals(accountService.findPlayersWithDeletedFalse().size(), 2);

    }
}