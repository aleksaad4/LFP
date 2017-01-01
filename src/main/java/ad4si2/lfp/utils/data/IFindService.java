package ad4si2.lfp.utils.data;

import ad4si2.lfp.utils.exceptions.DataNotFoundException;
import ad4si2.lfp.utils.validation.EntityValidator;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
public interface IFindService<ENTITY extends IDeleted & IEntity<ID, ENTITY>, ID extends Serializable,
        REPO extends RepositoryWithDeleted<ENTITY, ID>> extends EntityValidator<ENTITY> {

    @Nonnull
    REPO getRepo();

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
    @Override
    default EntityValidatorResult validateEntry(final ENTITY entry, final boolean forUpdate) {
        final EntityValidatorResult result = new EntityValidatorResult();
        return result.checkDeleted(entry);
    }
}
