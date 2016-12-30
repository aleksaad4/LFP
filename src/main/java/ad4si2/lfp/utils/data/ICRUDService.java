package ad4si2.lfp.utils.data;

import ad4si2.lfp.utils.exceptions.DataNotFoundException;
import ad4si2.lfp.utils.exceptions.LfpRuntimeException;
import ad4si2.lfp.utils.validation.EntityValidator;
import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Шаблонный сервис для работы с сущностями типа ответ,
 * стандартный ответ, атрибут и т. д. которые торчат на интерфейс и crud с него
 * <p>
 * Author:      daa
 * Date:        10.06.16
 * Company:     SofIT labs
 */
@Transactional
public interface ICRUDService<ENTITY extends IDeleted & IEntity<ID, ENTITY> & IAdminable, ID extends Serializable,
        REPO extends RepositoryWithDeleted<ENTITY, ID>> extends EntityValidator<ENTITY> {

    @Nonnull
    REPO getRepo();

    @Nonnull
    static EntityValidatorResult emptyValidatorResult() {
        return new EntityValidatorResult();
    }

    @Nonnull
    static EntityValidatorResult validatorResult(final String errorCode, final String errorMessage) {
        final EntityValidatorResult result = new EntityValidatorResult();
        result.addError(new EntityValidatorError(errorCode, errorMessage));
        return result;
    }

    @Nonnull
    static EntityValidatorResult validatorResult(final String fieldName, final String errorCode, final String errorMessage) {
        final EntityValidatorResult result = new EntityValidatorResult();
        result.addError(new EntityValidatorError(fieldName, errorCode, errorMessage));
        return result;
    }

    /**
     * @param deleted true, если нужно добавлять в выборку объекты с признаком deleted
     * @return список всех объектов
     */
    @Nonnull
    default List<ENTITY> findAll(final boolean deleted) {
        return deleted ? getRepo().findAll() : getRepo().findAllByDeletedFalse();
    }

    /**
     * @param ids     список id
     * @param deleted true, если нужно добавлять в выборку объекты с признаком deleted
     * @return список всех объектов с указанными id
     */
    @Nonnull
    default List<ENTITY> findAllByIdIn(@Nonnull final Set<ID> ids, final boolean deleted) {
        return ids.isEmpty() ? new ArrayList<>()
                : (deleted ? getRepo().findAllByIdIn(ids) : getRepo().findAllByDeletedFalseAndIdIn(ids));
    }

    /**
     * @param ids     список id
     * @param deleted true, если нужно добавлять в выборку объекты с признаком deleted
     * @return список всех объектов, исключая объекты с указанными id
     */
    @Nonnull
    default List<ENTITY> findAllByIdNotIn(@Nonnull final Set<ID> ids, final boolean deleted) {

        if (ids.isEmpty()) {
            return findAll(deleted);
        } else {
            return deleted ?
                    getRepo().findAllByIdNotIn(ids) :
                    getRepo().findAllByDeletedFalseAndIdNotIn(ids);
        }
    }

    /**
     * @param id      id
     * @param deleted true, если нужно добавлять в выборку объекты с признаком deleted
     * @return найденный объект или data not found exception, если не найден
     */
    @Nonnull
    default ENTITY getById(final ID id, final boolean deleted) {

        final ENTITY entity = findById(id, deleted);

        if (entity == null) {
            throw new DataNotFoundException("common_entry.deleted", "Объект [" + id + "] не найден");
        } else {
            return entity;
        }
    }

    /**
     * @param id      id
     * @param deleted true, если нужно добавлять в выборку объекты с признаком deleted
     * @return найденный объект или null если не найден
     */
    @Nullable
    default ENTITY findById(final ID id, final boolean deleted) {
        return deleted ? getRepo().findOne(id) : getRepo().findByIdAndDeletedFalse(id);
    }

    /**
     * @param id      id
     * @param deleted true, если нужно добавлять в выборку объекты с признаком deleted
     * @return optional от найденного (или ненайденного) объекта
     */
    default Optional<ENTITY> tryFindById(final ID id, final boolean deleted) {
        return Optional.ofNullable(findById(id, deleted));
    }

    @Nonnull
    default List<ENTITY> create(@Nonnull final Collection<ENTITY> ts) {
        final List<ENTITY> res = new ArrayList<>();
        for (final ENTITY t : ts) {
            res.add(create(t));
        }
        return res;
    }

    @Nonnull
    default ENTITY create(@Nonnull final ENTITY t) {
        return create(t, null, null);
    }

    default ENTITY update(@Nonnull final ENTITY t) {
        return update(t, null, null);
    }

    @Nonnull
    default ENTITY create(@Nonnull final ENTITY t, @Nullable final Consumer<ENTITY> preAction,
                          @Nullable final Consumer<ENTITY> postAction) {
        // валидируем создаваемый объект
        final EntityValidatorResult validatorResult = validateEntry(t, false);

        if (validatorResult.hasErrors()) {
            throw new LfpRuntimeException("Can't create [" + t + "], validation failed [" + validatorResult + "]");
        }

        // дополнительная обработка перед сохранением
        if (preAction != null) {
            preAction.accept(t);
        }

        // обновляем дату и admin-а
        // todo: set admin
        t.setD(new Date());

        // сохраняем
        final ENTITY saved = getRepo().save(t);

        // дополнительная обработка после сохранения
        if (postAction != null) {
            postAction.accept(t);
        }

        return saved;
    }

    @Nonnull
    default ENTITY update(@Nonnull final ENTITY t,
                          @Nullable final Consumer<ENTITY> preAction,
                          @Nullable final Consumer<ENTITY> postAction) {
        // валидируем редактируемый объект
        final EntityValidatorResult validatorResult = validateEntry(t, true);

        if (validatorResult.hasErrors()) {
            throw new LfpRuntimeException("Can't create [" + t + "], validation failed [" + validatorResult + "]");
        }

        // detached-old
        final ENTITY old = getById(t.getId(), false).copy();

        // дополнительная обработка перед сохранением
        if (preAction != null) {
            preAction.accept(t);
        }

        // обновляем дату и admin-а
        // todo: set admin
        t.setD(new Date());

        // сохраняем объект
        final ENTITY updated = getRepo().save(t);

        // дополнительная обработка после сохранения
        if (postAction != null) {
            preAction.accept(updated);
        }

        return updated;
    }

    default void delete(@Nonnull final ENTITY t) {
        delete(t, null);
    }

    default void delete(@Nonnull final ENTITY t, @Nullable final Consumer<ENTITY> preCheckF) {
        // проверки перед удалением
        if (preCheckF != null) {
            preCheckF.accept(t);
        }

        // помечаем удалённым и сохраняем
        final ENTITY item = getById(t.getId(), false);
        item.setDeleted(true);

        // обновляем дату и admin-а
        // todo: set admin
        t.setD(new Date());
    }

    default void delete(@Nonnull final Collection<ENTITY> ts) {
        for (final ENTITY t : ts) {
            delete(t);
        }
    }

    @Nonnull
    @Override
    default EntityValidatorResult validateEntry(final ENTITY entry, final boolean forUpdate) {
        return entry.isDeleted()
                ? validatorResult("common_entry.deleted", "Объект удален")
                : emptyValidatorResult();
    }
}
