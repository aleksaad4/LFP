package ad4si2.lfp.utils.data;

import ad4si2.lfp.utils.exceptions.ValidationException;
import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Transactional
public interface ICRUDService<ENTITY extends IDeleted & IEntity<ID, ENTITY>, ID extends Serializable,
        REPO extends RepositoryWithDeleted<ENTITY, ID>> extends IFindService<ENTITY, ID, REPO> {

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
            throw new ValidationException(validatorResult);
        }

        // дополнительная обработка перед сохранением
        if (preAction != null) {
            preAction.accept(t);
        }

        // сохраняем
        final ENTITY saved = getRepo().save(t);

        return saved;
    }

    @Nonnull
    default ENTITY update(@Nonnull final ENTITY t,
                          @Nullable final Consumer<ENTITY> preAction,
                          @Nullable final Consumer<ENTITY> postAction) {
        // валидируем редактируемый объект
        final EntityValidatorResult validatorResult = validateEntry(t, true);

        if (validatorResult.hasErrors()) {
            throw new ValidationException(validatorResult);
        }

        // дополнительная обработка перед сохранением
        if (preAction != null) {
            preAction.accept(t);
        }

        // сохраняем объект
        final ENTITY updated = getRepo().save(t);

        // дополнительная обработка после сохранения
        if (postAction != null) {
            postAction.accept(updated);
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
    }

    default void delete(@Nonnull final Collection<ENTITY> ts) {
        for (final ENTITY t : ts) {
            delete(t);
        }
    }

    @Nonnull
    default <T extends IEntity<T_ID, T>, T_ID> Consumer<T> dcc(@Nonnull final Function<T_ID, List<ENTITY>> finder,
                                                               @Nonnull final EntityValidatorResult result) {
        return p -> {
            final List<ENTITY> exists = finder.apply(p.getId());
            if (!exists.isEmpty()) {
                result.addError(new EntityValidatorError("Can't delete [" + p + "]", "common.can_t_delete"));
            }
        };
    }
}
