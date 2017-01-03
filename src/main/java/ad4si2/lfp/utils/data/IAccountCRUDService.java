package ad4si2.lfp.utils.data;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.utils.events.data.ChangeEvent;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.exceptions.LfpRuntimeException;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Date;
import java.util.function.Consumer;

@Transactional
public interface IAccountCRUDService<ENTITY extends IDeleted & IEntity<ID, ENTITY> & IAccountable, ID extends Serializable,
        REPO extends RepositoryWithDeleted<ENTITY, ID>> extends ICRUDService<ENTITY, ID, REPO> {

    @Nonnull
    ChangesEventDispatcher getEventDispatcher();

    @Nonnull
    WebEventsService getWebEventService();

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
        setAdminAndDate(t);

        // рассылаем PRE событие о создании нового объекта
        getEventDispatcher().dispatchEvent(ChangeEvent.createEvent(t, ChangeEvent.ChatChangeWhen.PRE));

        // сохраняем
        final ENTITY saved = getRepo().save(t);

        // дополнительная обработка после сохранения
        if (postAction != null) {
            postAction.accept(t);
        }

        // рассылаем POST событие о создании нового объекта
        getEventDispatcher().dispatchEvent(ChangeEvent.createEvent(t, ChangeEvent.ChatChangeWhen.POST));

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
        setAdminAndDate(t);

        // рассылаем PRE событие об обновлении объекта
        getEventDispatcher().dispatchEvent(ChangeEvent.updateEvent(t, old, ChangeEvent.ChatChangeWhen.PRE));

        // сохраняем объект
        final ENTITY updated = getRepo().save(t);

        // дополнительная обработка после сохранения
        if (postAction != null) {
            postAction.accept(updated);
        }

        // рассылаем POST событие об обновлении объекта
        getEventDispatcher().dispatchEvent(ChangeEvent.updateEvent(t, old, ChangeEvent.ChatChangeWhen.POST));

        return updated;
    }

    default void delete(@Nonnull final ENTITY t, @Nullable final Consumer<ENTITY> preCheckF) {
        // проверки перед удалением
        if (preCheckF != null) {
            preCheckF.accept(t);
        }

        // рассылаем PRE событие об удалении объекта
        getEventDispatcher().dispatchEvent(ChangeEvent.deleteEvent(t, ChangeEvent.ChatChangeWhen.PRE));

        // помечаем удалённым и сохраняем
        final ENTITY item = getById(t.getId(), false);
        item.setDeleted(true);

        // обновляем дату и admin-а
        setAdminAndDate(t);

        // рассылаем POST событие об удалении объекта
        getEventDispatcher().dispatchEvent(ChangeEvent.deleteEvent(t, ChangeEvent.ChatChangeWhen.POST));
    }

    default void setAdminAndDate(@Nonnull final ENTITY t) {
        final Account a = getWebEventService().getAccountFromEvent();
        if (a != null) {
            t.setAccountId(a.getId());
        }
        t.setD(new Date());
    }
}
