package ad4si2.lfp.utils.events.web;

import ad4si2.lfp.data.entities.account.Account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface WebEventsService {

    @Nonnull
    WebEvent startEvent(@Nonnull final WebEvent event);

    void finishEvent();

    boolean eventExists();

    @Nonnull
    WebEvent currentEvent();

    @Nullable
    Account getAccountFromEvent();
}
