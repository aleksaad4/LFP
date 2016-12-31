package ad4si2.lfp.utils.web;

import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WebError {

    @Nullable
    private String field;

    @Nonnull
    private String message;

    @Nullable
    private String exception;

    public WebError(@Nonnull final String message) {
        this.message = message;
    }

    public WebError(@Nullable final String field, @Nonnull final String message) {
        this.field = field;
        this.message = message;
    }

    public WebError(@Nonnull final String message, @Nonnull final Exception exception) {
        this.message = message;
        this.exception = ExceptionUtils.getStackTrace(exception);
    }

    @Nullable
    public String getField() {
        return field;
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

    @Nullable
    public String getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "WebError {" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}
