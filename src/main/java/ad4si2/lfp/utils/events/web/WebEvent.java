package ad4si2.lfp.utils.events.web;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WebEvent {

    @Nullable
    private Long accountId;

    @Nonnull
    private String ip;

    @Nonnull
    private String url;

    public WebEvent(@Nullable final Long accountId, @Nonnull final String ip, @Nonnull final String url) {
        this.accountId = accountId;
        this.ip = ip;
        this.url = url;
    }

    public WebEvent(@Nonnull final String ip, @Nonnull final String url) {
        this.ip = ip;
        this.url = url;
    }

    @Nullable
    public Long getAccountId() {
        return accountId;
    }

    @Nonnull
    public String getIp() {
        return ip;
    }

    @Nonnull
    public String getUrl() {
        return url;
    }

    public void setAccountId(@Nullable final Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "WebEvent {" +
                "accountId=" + accountId +
                ", ip='" + ip + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
