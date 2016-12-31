package ad4si2.lfp.web.interceptors;

import ad4si2.lfp.utils.web.WebUtils;
import ad4si2.lfp.utils.events.web.WebEvent;
import ad4si2.lfp.utils.events.web.WebEventsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class BaseInterceptor extends HandlerInterceptorAdapter {

    @Inject
    private WebEventsService webEventsService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        webEventsService.startEvent(new WebEvent(WebUtils.getIp(request), WebUtils.getRequestUrl(request)));

        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) throws Exception {
        webEventsService.finishEvent();

        super.postHandle(request, response, handler, modelAndView);
    }
}
