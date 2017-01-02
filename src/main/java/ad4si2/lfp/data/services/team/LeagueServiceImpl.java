package ad4si2.lfp.data.services.team;

import ad4si2.lfp.data.entities.team.League;
import ad4si2.lfp.data.repositories.team.LeagueRepository;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
@Transactional
public class LeagueServiceImpl implements LeagueService {

    @Inject
    private LeagueRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private CountryService countryService;

    @Nonnull
    @Override
    public LeagueRepository getRepo() {
        return repository;
    }

    @Nonnull
    @Override
    public ChangesEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final League entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = LeagueService.super.validateEntry(entry, forUpdate);

        // проверки на правильность заполнения полей
        result.checkIsEmpty("name", entry, League::getName)
                .checkMaxSize("name", 256, entry, League::getName);

        // проверки на уникальность
        result.checkDuplicate("name", entry, League::getName, name -> repository.findByNameAndDeletedFalse(name), forUpdate);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("country", entry, entry.getCountryId(), (linkedId) -> countryService.findById(linkedId, false), true);

        // если количество туров задано, оно должно быть больше 0
        if (entry.getTourCount() != null && entry.getTourCount() < 0) {
            result.addError(new EntityValidatorError("tourCount", "Incorrect value of tour count [" + entry.getTourCount() + "]", "league_tour_count_incorrect"));
        }

        return result;
    }
}
