package ad4si2.lfp.data.services.football;

import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.entities.football.MatchResult;
import ad4si2.lfp.data.entities.football.MatchStatus;
import ad4si2.lfp.data.entities.football.Team;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.repositories.football.MatchRepository;
import ad4si2.lfp.data.services.tour.TourService;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchServiceImpl implements MatchService, ChangesEventsListener {

    @Inject
    private MatchRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Inject
    private TeamService teamService;

    @Inject
    private TourService tourService;

    @Nonnull
    @Override
    public WebEventsService getWebEventService() {
        return webEventsService;
    }

    @Nonnull
    @Override
    public MatchRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nonnull
    @Override
    public List<Match> findByTourIdAndDeletedFalse(final long tourId) {
        return repository.findByTourIdAndDeletedFalse(tourId);
    }

    @Nonnull
    @Override
    public List<Match> findByTourIdInAndDeletedFalse(final Set<Long> tourIds) {
        return repository.findByTourIdInAndDeletedFalse(tourIds);
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final Match entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = MatchService.super.validateEntry(entry, forUpdate);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("teamAId", entry, entry.getTeamAId(), (linkedId) -> teamService.findById(linkedId, false), false)
                .checkLinkedValue("teamBId", entry, entry.getTeamBId(), (linkedId) -> teamService.findById(linkedId, false), false)
                .checkLinkedValue("tourId", entry, entry.getTourId(), (linkedId) -> tourService.findById(linkedId, false), false);

        return result;
    }

    @Nonnull
    @Override
    public EntityValidatorResult onEvent(@Nonnull final ChangeEvent event) {
        final EntityValidatorResult res = new EntityValidatorResult();

        event
                // нельзя удалить команду, если в системе уже есть матчи с этой командой
                .doIf(Team.class, ChangeEvent.ChangeEventType.PRE_DELETE,
                        dcc(l -> repository.findByTeamAIdOrTeamBId(l, l).stream().filter(m -> !m.isDeleted()).collect(Collectors.toList()), res))
                // нельзя удалить тур, если есть матчи в этом туре
                .doIf(Tour.class, ChangeEvent.ChangeEventType.PRE_DELETE, dcc(l -> repository.findByTourIdAndDeletedFalse(l), res))
                // нельзя удалить результат матча, если он уже завершен
                .doIf(MatchResult.class, ChangeEvent.ChangeEventType.PRE_DELETE, mr -> {
                    final Match match = getById(mr.getMatchId(), false);
                    if (match.getStatus() == MatchStatus.FINISH) {
                        res.addError(new EntityValidatorError("Can't delete [" + mr + "]", "common.can_t_delete"));
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
        return CollectionUtils.asSet(Team.class, Tour.class, MatchResult.class);
    }
}
