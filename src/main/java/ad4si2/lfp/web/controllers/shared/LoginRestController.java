package ad4si2.lfp.web.controllers.shared;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;
import ad4si2.lfp.web.interceptors.AuthInterceptor;
import ad4si2.lfp.web.menu.MenuBuilder;
import ad4si2.lfp.web.menu.MenuItem;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/")
public class LoginRestController {

    @Inject
    private AuthInterceptor authInterceptor;

    @Inject
    private AccountService accountService;

    @Inject
    private MenuBuilder menuBuilder;

    @Inject
    private WebUtils webUtils;

    @RequestMapping(value = "loginCheck")
    public AjaxResponse loginCheck(@Nonnull final HttpServletRequest request) {
        // достанем из сессии - он там уже должен быть, иначе бы интерцептор отбил запрос
        final Account account = authInterceptor.getAccount(request);
        if (account != null) {
            // авториован
            return webUtils.successResponse(new LoginResponseData(account, menuBuilder.getMenu(account)));
        } else {
            // не авторизован
            return webUtils.errorResponse(new ArrayList<>());
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public AjaxResponse login(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response,
                              @RequestBody final LoginRequestData loginRequestData) {
        // проверка входных данных
        final AjaxResponse errorResponse = validateLoginData(loginRequestData);
        if (errorResponse != null) {
            return errorResponse;
        }

        // noinspection ConstantConditions
        final Account account = accountService.findByLoginAndPasswordAndDeletedFalse(loginRequestData.getLogin(), loginRequestData.getPassword());
        if (account == null) {
            // невышло авторизоваться
            return webUtils.errorResponse(EntityValidatorResult.validatorResult("Incorrect login or password", "common.login_failed"));
        }

        // успешная авторизация
        authInterceptor.setInSession(request, account);

        // в зависимости от флага "Запомнить" добавляем или удаляем cookie
        if (loginRequestData.isRemember()) {
            authInterceptor.setInCookie(response, account);
        } else {
            authInterceptor.removeFromCookie(response);
        }

        // возвращаем аккаунт
        return webUtils.successResponse(new LoginResponseData(account, menuBuilder.getMenu(account)));
    }

    @RequestMapping(value = "account/logout")
    public AjaxResponse logout(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) {
        // удаляем из сессии
        authInterceptor.removeFromSession(request);
        // удаляем из cookie
        authInterceptor.removeFromCookie(response);
        return webUtils.successResponse("OK");
    }

    @Nullable
    private AjaxResponse validateLoginData(@Nonnull final LoginRequestData loginRequestData) {
        final EntityValidatorResult result = new EntityValidatorResult();
        if (loginRequestData.getLogin() == null || loginRequestData.getLogin().trim().isEmpty()) {
            result.addError(new EntityValidatorError("login", "Login is empty", "common.empty_login"));
        }
        if (loginRequestData.getPassword() == null || loginRequestData.getPassword().trim().isEmpty()) {
            result.addError(new EntityValidatorError("password", "Password is empty", "common.empty_password"));
        }
        if (result.hasErrors()) {
            return webUtils.errorResponse(result);
        }
        return null;
    }

    class LoginResponseData {

        @Nonnull
        private Account account;

        @Nonnull
        private List<MenuItem> menu;

        public LoginResponseData(@Nonnull final Account account, @Nonnull final List<MenuItem> menu) {
            this.account = account;
            this.menu = menu;
        }

        @Override
        public String toString() {
            return "LoginResponseData {" +
                    "menu=" + menu +
                    ", account=" + account +
                    '}';
        }
    }

    class LoginRequestData {

        @Nullable
        private String login;

        @Nullable
        private String password;

        private boolean isRemember;

        public LoginRequestData(@Nullable final String login, @Nullable final String password, final boolean isRemember) {
            this.login = login;
            this.password = password;
            this.isRemember = isRemember;
        }

        @Nullable
        public String getLogin() {
            return login;
        }

        @Nullable
        public String getPassword() {
            return password;
        }

        public boolean isRemember() {
            return isRemember;
        }

        @Override
        public String toString() {
            return "LoginData {" +
                    "login='" + login + '\'' +
                    ", password='" + password + '\'' +
                    ", isRemember=" + isRemember +
                    '}';
        }
    }
}
