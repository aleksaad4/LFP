package ad4si2.lfp.data.services.forecast;

import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.repositories.forecast.MeetingRepository;
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
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MeetingServiceImpl implements MeetingService, ChangesEventsListener {

    @Inject
    private MeetingRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Inject
    private AccountService accountService;

    @Inject
    private TourService tourService;

    @Nonnull
    @Override
    public WebEventsService getWebEventService() {
        return webEventsService;
    }

    @Nonnull
    @Override
    public MeetingRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nonnull
    @Override
    public List<Meeting> findByTourIdAndDeletedFalse(final long tourId) {
        return repository.findByTourIdAndDeletedFalse(tourId);
    }

    @Nonnull
    @Override
    public List<Meeting> findByTourIdInAndDeletedFalse(final Set<Long> tourIds) {
        return repository.findByTourIdInAndDeletedFalse(tourIds);
    }

    @Nullable
    @Override
    public Meeting findByTourIdAndPlayer(final long tourId, final long playerId) {
        final List<Meeting> meetings = repository.findByTourIdAndDeletedFalse(tourId).stream().filter(m -> m.getPlayerAId() == playerId || m.getPlayerBId() == playerId).collect(Collectors.toList());
        if (meetings.isEmpty()) {
            return null;
        } else {
            return meetings.get(0);
        }
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final Meeting entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = MeetingService.super.validateEntry(entry, forUpdate);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("playerAId", entry, entry.getPlayerAId(), (linkedId) -> accountService.findById(linkedId, false), false)
                .checkLinkedValue("playerBId", entry, entry.getPlayerBId(), (linkedId) -> accountService.findById(linkedId, false), false)
                .checkLinkedValue("tourId", entry, entry.getTourId(), (linkedId) -> tourService.findById(linkedId, false), false);

        return result;
    }

    @Nonnull
    @Override
    public EntityValidatorResult onEvent(@Nonnull final ChangeEvent event) {
        final EntityValidatorResult res = new EntityValidatorResult();

        event
                // после удаления тура удаляем и все встречи в этом туре
                // тур можно удалять только если он ещё не в открыт, так что никаких связных сущностей с этими встречами ещё не будет
                .doIf(Tour.class, ChangeEvent.ChangeEventType.POST_DELETE, t -> {
                    final List<Meeting> meetings = findByTourIdAndDeletedFalse(t.getId());
                    delete(meetings);
                });

        return res;
    }

    @Nonnull
    @Override
    public Set<ChangeEvent.ChangeEventType> getEventTypes() {
        return CollectionUtils.asSet(ChangeEvent.ChangeEventType.POST_DELETE);
    }

    @Nonnull
    @Override
    public Set<Class> getEntityTypes() {
        return CollectionUtils.asSet(Tour.class);
    }
}
