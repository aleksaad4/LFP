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
import javax.inject.Inject;
import java.util.Set;

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
                // нельзя удалить тур, если есть встречи в этом туре
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
