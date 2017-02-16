package ad4si2.lfp.web.interceptors;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.web.WebUtils;
import ad4si2.lfp.web.exceptions.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Service
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private static final String ACCOUNT_SESSION_ATTR = "ACCOUNT_SESSION_ATTR";

    private static final String COOKIE_KEY = "ACCOUNT_COOKIE_ATTR";

    private static final int COOKIE_MAX_AGE = (int) TimeUnit.DAYS.toMillis(365);

    @Inject
    private AccountService accountService;

    @Inject
    private WebEventsService webEventsService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        // получаем аккаунт
        final Account account = getAccount(request);
        if (account == null) {
            throw new UnauthorizedException("common.access_denied", "Can't authorize account");
        }

        // проставляем аккаунт в event
        webEventsService.currentEvent().setAccountId(account.getId());

        return super.preHandle(request, response, handler);
    }

    @Nonnull
    public Account currentAccount(@Nonnull final HttpServletRequest request) {
        final Account account = getAccount(request);
        if (account == null) {
            throw new UnauthorizedException("common.access_denied", "Can't authorize account");
        } else {
            return account;
        }
    }

    @Nullable
    public Account getAccount(final HttpServletRequest request) {
        // получаем аккаунт из сессии
        Account account = getFromSession(request);
        if (account == null) {
            // пытаемся получить акккаунт из куки
            account = getFromCookie(request);

            if (account != null) {
                // запоминаем account в сессию
                setInSession(request, account);
            }
        }

        return account;
    }

    @Nullable
    public Account getFromSession(@Nonnull final HttpServletRequest request) {
        return (Account) request.getSession(true).getAttribute(ACCOUNT_SESSION_ATTR);
    }

    public void setInSession(@Nonnull final HttpServletRequest request, @Nonnull final Account account) {
        request.getSession(true).setAttribute(ACCOUNT_SESSION_ATTR, account);
    }

    public void removeFromSession(@Nonnull final HttpServletRequest request) {
        request.getSession(true).removeAttribute(ACCOUNT_SESSION_ATTR);
    }

    public void setInCookie(@Nonnull final HttpServletResponse response, @Nonnull final Account account) {
        WebUtils.setCookieValue(response, COOKIE_KEY, account.getId() + "", COOKIE_MAX_AGE);
    }

    public void removeFromCookie(@Nonnull final HttpServletResponse response) {
        WebUtils.removeCookieValue(response, COOKIE_KEY);
    }

    @Nullable
    private Account getFromCookie(@Nonnull final HttpServletRequest request) {
        final String cookieValue = WebUtils.getCookieValue(request, COOKIE_KEY);
        if (cookieValue != null) {
            return accountService.findById(Long.parseLong(cookieValue), false);
        }
        return null;
    }
}
