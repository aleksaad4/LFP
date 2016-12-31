package ad4si2.lfp.utils.events.data;

import javax.annotation.Nonnull;

public interface ChangesEventDispatcher {

    void dispatchEvent(@Nonnull final ChangeEvent changeEvent);
}
