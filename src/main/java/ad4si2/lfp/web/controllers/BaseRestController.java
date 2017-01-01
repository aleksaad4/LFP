package ad4si2.lfp.web.controllers;

import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.utils.data.IAccountCRUDService;
import ad4si2.lfp.utils.data.IAccountable;
import ad4si2.lfp.utils.data.IDeleted;
import ad4si2.lfp.utils.data.IEntity;
import ad4si2.lfp.utils.validation.EntityValidator;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

public abstract class BaseRestController<ID extends Serializable,
        ENTITY extends IDeleted & IEntity<ID, ENTITY> & IAccountable,
        SERVICE extends IAccountCRUDService<ENTITY, ID, ?> & EntityValidator<ENTITY>> {

    @Inject
    protected AccountService accountService;

    @Inject
    protected WebUtils webUtils;

    protected abstract SERVICE getService();

    public AjaxResponse list() {
        // список
        final List<ENTITY> all = getService().findAll(false);
        // подгружаем аккаунты
        all.forEach(this::setAccount);

        return webUtils.successResponse(all);
    }

    public AjaxResponse create(@Nonnull final ENTITY entity) {
        // валидация
        final EntityValidatorResult validatorResult = getService().validateEntry(entity, false);
        if (validatorResult.hasErrors()) {
            return webUtils.errorResponse(validatorResult);
        }

        // сохранение в БД
        final ENTITY created = getService().create(entity);
        setAccount(created);

        return webUtils.successResponse(created);
    }

    public AjaxResponse update(@Nonnull final ENTITY entity) {
        // валидация
        final EntityValidatorResult validatorResult = getService().validateEntry(entity, true);
        if (validatorResult.hasErrors()) {
            return webUtils.errorResponse(validatorResult);
        }

        // сохранение в БД
        final ENTITY updated = getService().update(entity);
        setAccount(updated);

        return webUtils.successResponse(updated);
    }

    public AjaxResponse get(@Nonnull final ID id) {
        final ENTITY e = getService().getById(id, false);
        setAccount(e);

        return webUtils.successResponse(e);
    }

    public AjaxResponse delete(@Nonnull final ID id) {
        final ENTITY entity = getService().getById(id, false);

        // удаляем из БД
        getService().delete(entity);

        return webUtils.successResponse("OK");
    }

    protected void setAccount(@Nonnull final ENTITY e) {
        if (e.getAccountId() != null) {
            e.setAccount(accountService.getById(e.getAccountId(), true));
        }
    }

}
