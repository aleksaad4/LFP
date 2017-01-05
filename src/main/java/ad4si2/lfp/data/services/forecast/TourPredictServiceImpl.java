package ad4si2.lfp.data.services.forecast;

import ad4si2.lfp.data.entities.forecast.TourPredict;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.repositories.forecast.TourPredictRepository;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.data.services.tour.TourService;
import ad4si2.lfp.utils.collection.CollectionUtils;
import ad4si2.lfp.utils.events.data.ChangeEvent;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.data.ChangesEventsListener;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

@Service
@Transactional
public class TourPredictServiceImpl implements TourPredictService, ChangesEventsListener {

    @Inject
    private TourPredictRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Inject
    private TourService tourService;

    @Inject
    private AccountService accountService;

    @Nonnull
    @Override
    public WebEventsService getWebEventService() {
        return webEventsService;
    }

    @Nonnull
    @Override
    public TourPredictRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final TourPredict entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = TourPredictService.super.validateEntry(entry, forUpdate);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("tourId", entry, entry.getTourId(), (linkedId) -> tourService.findById(linkedId, false), false)
                .checkLinkedValue("playerId", entry, entry.getPlayerId(), (linkedId) -> accountService.findById(linkedId, false), false);

        // количество очков >= нуля
        result.checkValue("score", entry, TourPredict::getScore, val -> val >= 0);

        return result;
    }

    @Nonnull
    @Override
    public EntityValidatorResult onEvent(@Nonnull final ChangeEvent event) {
        final EntityValidatorResult res = new EntityValidatorResult();

        event
                // нельзя удалить тур, если есть прогнозы на этот тур
                .doIf(Tour.class, ChangeEvent.ChangeEventType.PRE_DELETE, dcc(l -> repository.findByTourIdAndDeletedFalse(l), res));

        return res;
    }

    @Nonnull
    @Override
    public Set<ChangeEvent.ChangeEventType> getEventTypes() {
        return CollectionUtils.asSet(ChangeEvent.ChangeEventType.PRE_DELETE);
    }

    @Nonnull
    @Override
    public Set<Class> getEntityTypes() {
        return CollectionUtils.asSet(Tour.class);
    }
}
