package ad4si2.lfp.utils.web;

import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebUtils {

    @Inject
    private MessageSource messageSource;

    public static void setCookieValue(@Nonnull final HttpServletResponse response,
                                      @Nonnull final String cookieKey,
                                      @Nonnull final String cookieValue,
                                      final int cookieMaxAge) {
        final Cookie cookie = new Cookie(cookieKey, cookieValue);
        cookie.setMaxAge(cookieMaxAge);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void removeCookieValue(@Nonnull final HttpServletResponse response,
                                         @Nonnull final String cookieKey) {
        final Cookie cookie = new Cookie(cookieKey, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    @Nullable
    public static String getCookieValue(@Nonnull final HttpServletRequest request, @Nonnull final String cookieKey) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieKey) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Nonnull
    public static String getIp(@Nonnull final HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Nonnull
    public static String getRequestUrl(@Nonnull final HttpServletRequest request) {
        return request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    }

    @Nonnull
    public AjaxResponse errorResponse(@Nonnull final EntityValidatorResult result) {
        return errorResponse(result.getErrors());
    }

    @Nonnull
    public AjaxResponse errorResponse(@Nonnull final List<EntityValidatorError> errors) {
        return new AjaxResponse(AjaxResponse.Result.ERROR, null,
                errors.stream().map(e -> new WebError(e.getFieldKey(), e.getTextForUser(messageSource))).collect(Collectors.toList()));
    }

    @Nonnull
    public AjaxResponse successResponse(@Nonnull final Object result) {
        return new AjaxResponse(AjaxResponse.Result.OK, result, null);
    }
}
