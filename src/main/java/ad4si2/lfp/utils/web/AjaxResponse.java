package ad4si2.lfp.utils.web;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class AjaxResponse {

    @Nonnull
    private Result status;

    @Nullable
    private Object result;

    @Nullable
    private List<WebError> errors;

    public AjaxResponse(@Nonnull final Result status, @Nullable final Object result, @Nullable final List<WebError> errors) {
        this.status = status;
        this.result = result;
        this.errors = errors;
    }

    @Nonnull
    public Result getStatus() {
        return status;
    }

    @Nullable
    public Object getResult() {
        return result;
    }

    @Nullable
    public List<WebError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "AjaxResponse {" +
                "status=" + status +
                ", result=" + result +
                ", errors=" + errors +
                '}';
    }

    public enum Result {
        OK, ERROR, ACCESS_DENIED
    }
}
