package ad4si2.lfp.utils.events.data;

import ad4si2.lfp.utils.validation.EntityValidatorResult;

import javax.annotation.Nonnull;
import java.util.Set;

public interface ChangesEventsListener {

    @Nonnull
    EntityValidatorResult onEvent(@Nonnull ChangeEvent event);

    @Nonnull
    Set<ChangeEvent.ChangeEventType> getEventTypes();

    @Nonnull
    Set<Class> getEntityTypes();
}
