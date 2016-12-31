package ad4si2.lfp.utils.events.data;

import javax.annotation.Nonnull;
import java.util.Set;

public interface ChangesEventsListener {

    void onEvent(@Nonnull ChangeEvent event);

    @Nonnull
    Set<ChangeEvent.ChangeEventType> getEventTypes();

    @Nonnull
    Set<Class> getEntityTypes();
}
