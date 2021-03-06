package ad4si2.lfp.data.services.football;

import ad4si2.lfp.data.entities.football.Country;
import ad4si2.lfp.data.entities.football.Team;
import ad4si2.lfp.data.repositories.football.TeamRepository;
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
public class TeamServiceImpl implements TeamService, ChangesEventsListener {

    @Inject
    private TeamRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Inject
    private CountryService countryService;

    @Nonnull
    @Override
    public TeamRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nonnull
    @Override
    public WebEventsService getWebEventService() {
        return webEventsService;
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final Team entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = TeamService.super.validateEntry(entry, forUpdate);

        // проверки на правильность заполнения полей
        result.checkIsEmpty("name", entry, Team::getName)
                .checkMaxSize("name", 256, entry, Team::getName)
                .checkIsEmpty("city", entry, Team::getCity)
                .checkMaxSize("city", 256, entry, Team::getCity);

        // проверки на уникальность
        result.checkDuplicate("name", "city", entry, Team::getName, Team::getCity, (name, city) -> repository.findByNameAndCityAndDeletedFalse(name, city), forUpdate);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("country", entry, entry.getCountryId(), (linkedId) -> countryService.findById(linkedId, false), true);

        return result;
    }

    @Nonnull
    @Override
    public EntityValidatorResult onEvent(@Nonnull final ChangeEvent event) {
        final EntityValidatorResult res = new EntityValidatorResult();

        event
                // нельзя удалить страну, если есть команда из этой страны
                .doIf(Country.class, ChangeEvent.ChangeEventType.PRE_DELETE, dcc(l -> repository.findByCountryIdAndDeletedFalse(l), res));

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
        return CollectionUtils.asSet(Country.class);
    }
}
