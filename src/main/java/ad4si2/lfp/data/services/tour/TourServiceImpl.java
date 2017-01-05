package ad4si2.lfp.data.services.tour;

import ad4si2.lfp.data.entities.forecast.MatchPredict;
import ad4si2.lfp.data.entities.forecast.TourPredict;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.data.repositories.tour.TourRepository;
import ad4si2.lfp.data.services.forecast.TourPredictService;
import ad4si2.lfp.data.services.tournament.TournamentService;
import ad4si2.lfp.utils.collection.CollectionUtils;
import ad4si2.lfp.utils.events.data.ChangeEvent;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.data.ChangesEventsListener;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

@Service
@Transactional
public class TourServiceImpl implements TourService, ChangesEventsListener {

    @Inject
    private TourRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Inject
    private TournamentService tournamentService;

    @Inject
    private TourPredictService tourPredictService;

    @Nonnull
    @Override
    public TourRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public WebEventsService getWebEventService() {
        return webEventsService;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final Tour entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = TourService.super.validateEntry(entry, forUpdate);

        // проверки на правильность заполнения полей
        result.checkIsEmpty("name", entry, Tour::getName)
                .checkMaxSize("name", 256, entry, Tour::getName);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("tournamentId", entry, entry.getTournamentId(), (linkedId) -> tournamentService.findById(linkedId, false), false);

        return result;
    }

    @Nonnull
    @Override
    public EntityValidatorResult onEvent(@Nonnull final ChangeEvent event) {
        final EntityValidatorResult res = new EntityValidatorResult();

        event
                // нельзя удалить прогноз на тур, если тур стартовал
                .doIf(MatchPredict.class, ChangeEvent.ChangeEventType.PRE_DELETE, mp -> {
                    final TourPredict tp = tourPredictService.getById(mp.getTourPredictId(), false);
                    final Tour tour = getById(tp.getTourId(), false);
                    if (tour.getStatus() != TourStatus.OPEN || tour.getStatus() != TourStatus.NOT_STARTED) {
                        res.addError(new EntityValidatorError("Can't delete [" + mp + "]", "common.can_t_delete"));
                    }
                });

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
        return CollectionUtils.asSet(MatchPredict.class);
    }
}
