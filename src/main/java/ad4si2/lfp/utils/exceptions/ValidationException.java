package ad4si2.lfp.utils.exceptions;

import ad4si2.lfp.utils.validation.EntityValidatorResult;

import javax.annotation.Nonnull;

public class ValidationException extends RuntimeException {

    @Nonnull
    private EntityValidatorResult result;

    public ValidationException(final EntityValidatorResult result) {
        this.result = result;
    }
}

