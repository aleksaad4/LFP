package ad4si2.lfp.data.services.team;

import ad4si2.lfp.data.entities.team.Team;
import ad4si2.lfp.data.repositories.team.TeamRepository;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Inject
    private TeamRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

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
}
