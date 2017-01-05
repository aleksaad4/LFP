package ad4si2.lfp.data.services.football;

import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.entities.football.MatchResult;
import ad4si2.lfp.data.repositories.football.MatchResultRepository;
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
public class MatchResultServiceImpl implements MatchResultService, ChangesEventsListener{

    @Inject
    private MatchResultRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Inject
    private MatchService matchService;

    @Nonnull
    @Override
    public WebEventsService getWebEventService() {
        return webEventsService;
    }

    @Nonnull
    @Override
    public MatchResultRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final MatchResult entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = MatchResultService.super.validateEntry(entry, forUpdate);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("matchId", entry, entry.getMatchId(), (linkedId) -> matchService.findById(linkedId, false), false);

        // количество голов должно быть >= 0
        result.checkValue("aGoals", entry, MatchResult::getaGoals, val -> val >= 0)
                .checkValue("bGoals", entry, MatchResult::getbGoals, val -> val >= 0);

        return result;
    }

    @Nonnull
    @Override
    public EntityValidatorResult onEvent(@Nonnull final ChangeEvent event) {
        final EntityValidatorResult res = new EntityValidatorResult();

        event
                // нельзя матч, если есть результаты этого матча
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
