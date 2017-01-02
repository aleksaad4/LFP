package ad4si2.lfp.web.controllers;

import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.utils.data.*;
import ad4si2.lfp.utils.validation.EntityValidator;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        fillEntityList(all);

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
        fillEntity(created);

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
        fillEntity(updated);

        return webUtils.successResponse(updated);
    }

    public AjaxResponse get(@Nonnull final ID id) {
        final ENTITY e = getService().getById(id, false);
        fillEntity(e);

        return webUtils.successResponse(e);
    }

    public AjaxResponse delete(@Nonnull final ID id) {
        final ENTITY entity = getService().getById(id, false);

        // удаляем из БД
        getService().delete(entity);

        return webUtils.successResponse("OK");
    }

    /**
     * Заполняем дополнительные поля перед возвращением сущности
     *
     * @param entity entity
     */
    protected void fillEntity(@Nonnull final ENTITY entity) {
        // аккаунт
        fillLinkedValues(entity, e -> e.getAccountId(), accountService, (e, le) -> e.setAccount(le));
    }

    /**
     * Заполняем дополнительные поля перед возвращением сущности
     *
     * @param entities list of entity
     */
    protected void fillEntityList(@Nonnull final List<ENTITY> entities) {
        // аккаунт
        fillLinkedValues(entities, e -> e.getAccountId(), accountService, (e, le) -> e.setAccount(le));
    }

    /**
     * Метод для заполнения дополнительного поля сущности перед возвращением её на front
     * <p>
     * Находим связанную сущность по id и проставляем её
     *
     * @param entity          исходная сущность
     * @param linkedIdGetter  метод для получения id связанной сущности
     * @param service         сервис для получения связанной сущности
     * @param setter          метод для выставления связанной сущности в исходную сущность
     * @param <LINKED_ENTITY> тип связанной сущности
     * @param <LINKED_ID>     тип исходной сущности
     */
    protected <LINKED_ENTITY extends IDeleted & IEntity<LINKED_ID, LINKED_ENTITY>, LINKED_ID extends Serializable>
    void fillLinkedValues(@Nonnull final ENTITY entity,
                          @Nonnull final Function<ENTITY, LINKED_ID> linkedIdGetter,
                          @Nonnull final IFindService<LINKED_ENTITY, LINKED_ID, ?> service,
                          @Nonnull final BiConsumer<ENTITY, LINKED_ENTITY> setter) {
        if (linkedIdGetter.apply(entity) != null) {
            setter.accept(entity, service.findById(linkedIdGetter.apply(entity), true));
        }
    }

    /**
     * Метод для заполнения дополнительного поля в каждой сущности из списка в перед возвращением её на front
     * <p>
     * Достаём одним запросом все необходимые связанные сущности
     * И потом пробегаемся по списке сущностей и проставляем связанную сущность в каждой из них
     *
     * @param entities        список исходных сущностей
     * @param linkedIdGetter  метод для получения id связанной сущности
     * @param service         сервис для получения связанной сущности
     * @param setter          метод для выставления связанной сущности в исходную сущность
     * @param <LINKED_ENTITY> тип связанной сущности
     * @param <LINKED_ID>     тип исходной сущности
     */
    protected <LINKED_ENTITY extends IDeleted & IEntity<LINKED_ID, LINKED_ENTITY>, LINKED_ID extends Serializable>
    void fillLinkedValues(@Nonnull final List<ENTITY> entities,
                          @Nonnull final Function<ENTITY, LINKED_ID> linkedIdGetter,
                          @Nonnull final IFindService<LINKED_ENTITY, LINKED_ID, ?> service,
                          @Nonnull final BiConsumer<ENTITY, LINKED_ENTITY> setter) {
        final Set<LINKED_ID> ids = entities.stream().filter(e -> linkedIdGetter.apply(e) != null).map(linkedIdGetter).collect(Collectors.toSet());
        final Map<LINKED_ID, LINKED_ENTITY> id2linkedEntity = service.findAllByIdIn(ids, true).stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
        entities.stream().filter(e -> linkedIdGetter.apply(e) != null).forEach((e) -> setter.accept(e, id2linkedEntity.get(linkedIdGetter.apply(e))));
    }
}
