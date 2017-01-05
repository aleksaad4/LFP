package ad4si2.lfp.utils.events.data;

import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChangesEventDispatcherImpl implements ChangesEventDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangesEventDispatcherImpl.class);

    @Inject
    private ApplicationContext applicationContext;

    @Override
    @Nonnull
    public EntityValidatorResult dispatchEvent(@Nonnull final ChangeEvent changeEvent) {
        final EntityValidatorResult result = new EntityValidatorResult();

        // отфильтруем слушателей
        final List<ChangesEventsListener> listeners = selectListeners(changeEvent);

        for (final ChangesEventsListener listener : listeners) {
            // POST-события
            if (changeEvent.getChangeEventType().isPostEvent()) {
                // пост-события начинаем рассылать после того, как транзакция с основным (и пре) событиями будет комичена
                if (TransactionSynchronizationManager.isActualTransactionActive()) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCommit() {
                            try {
                                sendEventWithoutTransaction(changeEvent, listener);
                            } catch (final Exception e) {
                                LOGGER.error("Error during handle event [" + changeEvent + "] for listener [" + listener + "]", e);
                            }
                        }
                    });
                } else {
                    // если транзакции нет, то просто отправляем ивент
                    try {
                        sendEventWithoutTransaction(changeEvent, listener);
                    } catch (final Exception e) {
                        LOGGER.error("Error during handle event [" + changeEvent + "] for listener [" + listener + "]", e);
                    }
                }
            } else {
                // PRE-события
                result.addErrors(listener.onEvent(changeEvent).getErrors());
            }
        }

        return result;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    private void sendEventWithoutTransaction(final ChangeEvent changeEvent, final ChangesEventsListener listener) {
        // делаем REQUIRES_NEW, потому что commit идёт внутри старой транзакции иначе (см. doc к afterCommit)
        listener.onEvent(changeEvent);
    }

    @Nonnull
    private List<ChangesEventsListener> selectListeners(@Nonnull final ChangeEvent changeEvent) {
        final List<ChangesEventsListener> listeners = new ArrayList<>();
        final Map<String, ChangesEventsListener> listenerMap = getListeners(ChangesEventsListener.class);

        for (final ChangesEventsListener listener : listenerMap.values()) {
            // не тот тип события
            if (!listener.getEventTypes().contains(changeEvent.getChangeEventType())) {
                continue;
            }

            // слушателю не подходит событие
            if (listener.getEntityTypes().stream().noneMatch(et -> et.isInstance(changeEvent.getObject()))) {
                continue;
            }

            listeners.add(listener);
        }
        return listeners;
    }

    @Nonnull
    private <T> Map<String, T> getListeners(@Nonnull final Class<T> listenerClass) throws BeansException {
        return applicationContext.getBeansOfType(listenerClass);
    }
}
