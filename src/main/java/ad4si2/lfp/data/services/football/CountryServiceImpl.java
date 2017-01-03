package ad4si2.lfp.data.services.football;

import ad4si2.lfp.data.entities.football.Country;
import ad4si2.lfp.data.repositories.football.CountryRepository;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    @Inject
    private CountryRepository repository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Nonnull
    @Override
    public CountryRepository getRepo() {
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
    public EntityValidatorResult validateEntry(final Country entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = CountryService.super.validateEntry(entry, forUpdate);

        // проверки на правильность заполнения полей
        result.checkIsEmpty("name", entry, Country::getName)
                .checkMaxSize("name", 256, entry, Country::getName);

        // проверки на уникальность
        result.checkDuplicate("name", entry, Country::getName, name -> repository.findByNameAndDeletedFalse(name), forUpdate);

        return result;
    }
}
