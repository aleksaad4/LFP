package ad4si2.lfp.utils.events.web;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.utils.exceptions.LfpRuntimeException;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

@Service
public class WebEventsServiceImpl implements WebEventsService {

    private static final ThreadLocal<WebEvent> EVENT = new ThreadLocal<>();

    @Inject
    private AccountService accountService;

    @Nonnull
    @Override
    public WebEvent startEvent(@Nonnull final WebEvent event) {
        EVENT.set(event);
        return event;
    }

    @Override
    public void finishEvent() {
        EVENT.set(null);
    }

    @Override
    public boolean eventExists() {
        return EVENT.get() != null;
    }

    @Nonnull
    @Override
    public WebEvent currentEvent() {
        if (!eventExists()) {
            throw new LfpRuntimeException("FAIL, current event does not exists");
        }
        return EVENT.get();
    }

    @Nullable
    @Override
    public Account getAccountFromEvent() {
        if (eventExists()) {
            final Long accountId = currentEvent().getAccountId();
            if (accountId != null) {
                return accountService.findById(accountId, true);
            }
        }
        return null;
    }
}
