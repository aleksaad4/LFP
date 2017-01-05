package ad4si2.lfp.utils.events.data;

import ad4si2.lfp.utils.validation.EntityValidatorResult;

import javax.annotation.Nonnull;

public interface ChangesEventDispatcher {

    @Nonnull
    EntityValidatorResult dispatchEvent(@Nonnull final ChangeEvent changeEvent);
}
