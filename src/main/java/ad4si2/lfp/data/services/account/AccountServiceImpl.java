package ad4si2.lfp.data.services.account;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.AccountRole;
import ad4si2.lfp.data.repositories.account.AccountRepository;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService, ApplicationListener<ApplicationReadyEvent> {

    @Inject
    private AccountRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Nonnull
    @Override
    public AccountRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public WebEventsService getWebEventService() {
        return webEventsService;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nullable
    @Override
    public Account findByLoginAndPasswordAndDeletedFalse(@Nonnull final String login, @Nonnull final String password) {
        final List<Account> accounts = repository.findByLoginAndPasswordAndDeletedFalse(login, password);
        if (accounts.isEmpty()) {
            return null;
        } else {
            return accounts.get(0);
        }
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final Account entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = AccountService.super.validateEntry(entry, forUpdate);

        // проверки на правильность заполнения полей
        result.checkIsEmpty("login", entry, Account::getLogin)
                .checkMaxSize("login", 256, entry, Account::getLogin)
                .checkIsEmpty("password", entry, Account::getPassword)
                .checkMaxSize("password", 256, entry, Account::getPassword)
                .checkMaxSize("name", 256, entry, Account::getName)
                .checkMaxSize("email", 256, entry, Account::getEmail)
                .checkIsNull("role", entry, Account::getRole);

        // проверки на уникальность
        result.checkDuplicate("login", entry, Account::getLogin, login -> repository.findByLoginAndDeletedFalse(login), forUpdate);

        if (forUpdate) {
            // нельзя обновлять тип аккаунта
            result.checkNotModify("role", entry, findById(entry.getId(), false), Account::getRole);
        }

        return result;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
        // создаём админа admin/admin, если его нет
        if (repository.findByLoginAndDeletedFalse("admin").isEmpty()) {
            create(new Account("admin", "admin", AccountRole.ADMIN));
        }
    }
}
