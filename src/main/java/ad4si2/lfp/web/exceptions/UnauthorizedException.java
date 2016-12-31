package ad4si2.lfp.web.exceptions;

import ad4si2.lfp.utils.exceptions.LfpRuntimeException;

public class UnauthorizedException extends LfpRuntimeException {

    public UnauthorizedException(final String message) {
        super(message);
    }

    public UnauthorizedException(final Throwable cause) {
        super(cause);
    }

    public UnauthorizedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(final String userMessageKey, final String message) {
        super(userMessageKey, message);
    }

    public UnauthorizedException(final String userMessageKey, final String message, final Throwable cause) {
        super(userMessageKey, message, cause);
    }
}
