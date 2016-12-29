package ad4si2.lfp.utils.exceptions;

public class DataNotFoundException extends LfpRuntimeException {

    public DataNotFoundException(final String message) {
        super(message);
    }

    public DataNotFoundException(final Throwable cause) {
        super(cause);
    }

    public DataNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DataNotFoundException(final String userMessageKey, final String message) {
        super(userMessageKey, message);
    }

    public DataNotFoundException(final String userMessageKey, final String message, final Throwable cause) {
        super(userMessageKey, message, cause);
    }
}
