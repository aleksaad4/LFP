package ad4si2.lfp.data.services.forecast;

import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.entities.forecast.MatchPredict;
import ad4si2.lfp.data.repositories.forecast.MatchPredictRepository;
import ad4si2.lfp.data.services.football.MatchService;
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
public class MatchPredictServiceImpl implements MatchPredictService, ChangesEventsListener {

    @Inject
    private MatchPredictRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Inject
    private MatchService matchService;

    @Inject
    private TourPredictService tourPredictService;

    @Nonnull
    @Override
    public WebEventsService getWebEventService() {
        return webEventsService;
    }

    @Nonnull
    @Override
    public MatchPredictRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final MatchPredict entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = MatchPredictService.super.validateEntry(entry, forUpdate);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("matchId", entry, entry.getMatchId(), (linkedId) -> matchService.findById(linkedId, false), false)
                .checkLinkedValue("tourPredictId", entry, entry.getTourPredictId(), (linkedId) -> tourPredictService.findById(linkedId, false), false);

        // количество голов должно быть >= 0
        result.checkValue("aGoals", entry, MatchPredict::getaGoals, val -> val >= 0)
                .checkValue("bGoals", entry, MatchPredict::getbGoals, val -> val >= 0);

        return result;
    }

    @Nonnull
    @Override
    public EntityValidatorResult onEvent(@Nonnull final ChangeEvent event) {
        final EntityValidatorResult res = new EntityValidatorResult();

        event
                // нельзя удалить матч, если есть прогнозы на этот матч
                .doIf(Match.class, ChangeEvent.ChangeEventType.PRE_DELETE, dcc(l -> repository.findByMatchIdAndDeletedFalse(l), res));

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
        return CollectionUtils.asSet(Match.class);
    }
}
