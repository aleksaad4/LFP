package ad4si2.lfp.utils.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LfpRuntimeException extends RuntimeException {

    private String userMessageKey;

    public LfpRuntimeException(final String message) {
        super(message);
    }

    public LfpRuntimeException(final Throwable cause) {
        super(cause);
        userMessageKey = fetchUserMessageKey(cause);
    }

    public LfpRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
        userMessageKey = fetchUserMessageKey(cause);
    }

    public LfpRuntimeException(final String userMessageKey, final String message) {
        super(message);
        this.userMessageKey = userMessageKey;
    }

    public LfpRuntimeException(final String userMessageKey, final String message, final Throwable cause) {
        super(message, cause);
        this.userMessageKey = userMessageKey;
    }

    public String getUserMessageKey() {
        return userMessageKey;
    }

    @Nonnull
    public String getTextForUser(@Nonnull final MessageSource messageSource) {
        if (userMessageKey != null) {
            try {
                return messageSource.getMessage(userMessageKey, null, null);
            } catch (final NoSuchMessageException e) {
                return "Неизвестная ошибка: " + getMessage() + " (" + userMessageKey + ") ";
            }
        } else {
            return "Неизвестная ошибка: " + getMessage();
        }
    }

    @Nullable
    private static String fetchUserMessageKey(final Throwable throwable) {
        if (throwable instanceof LfpRuntimeException) {
            return ((LfpRuntimeException) throwable).getUserMessageKey();
        }

        //noinspection ThrowableResultOfMethodCallIgnored
        final Throwable cause = ExceptionUtils.getRootCause(throwable);

        if (cause instanceof LfpRuntimeException) {
            return ((LfpRuntimeException) cause).getUserMessageKey();
        }

        return null;
    }
}

