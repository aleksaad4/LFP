package ad4si2.lfp.data.services.tour;

import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.entities.forecast.MatchPredict;
import ad4si2.lfp.data.entities.forecast.TourPredict;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.data.entities.tournament.Championship;
import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.data.repositories.tour.TourRepository;
import ad4si2.lfp.data.services.football.MatchService;
import ad4si2.lfp.data.services.forecast.TourPredictService;
import ad4si2.lfp.data.services.tournament.TournamentService;
import ad4si2.lfp.utils.collection.CollectionUtils;
import ad4si2.lfp.utils.date.DateUtils;
import ad4si2.lfp.utils.events.data.ChangeEvent;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.data.ChangesEventsListener;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.exceptions.LfpRuntimeException;
import ad4si2.lfp.utils.exceptions.ValidationException;
import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

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

    @Inject
    private MatchService matchService;

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
    public List<Tour> findByTournamentIdAndDeletedFalse(final long tId) {
        return repository.findByTournamentIdAndDeletedFalse(tId);
    }

    @Nonnull
    @Override
    public List<Tour> findByOpenDateAndStatus(@Nonnull final Date date, @Nonnull final TourStatus status) {
        final Pair<Date, Date> dates = DateUtils.getDayDates(date);
        return repository.findByOpenDateBeforeAndDeletedFalseAndStatus(dates.getRight(), status);
    }

    @Nonnull
    @Override
    public List<Tour> findByStartDateAndStatus(@Nonnull final Date date, @Nonnull final TourStatus status) {
        final Pair<Date, Date> dates = DateUtils.getDayDates(date);
        return repository.findByStartDateBeforeAndDeletedFalseAndStatus(dates.getRight(), status);
    }

    @Nonnull
    @Override
    public List<Tour> findByFinishDateAndStatus(@Nonnull final Date date, @Nonnull final TourStatus status) {
        final Pair<Date, Date> dates = DateUtils.getDayDates(date);
        return repository.findByFinishDateBeforeAndDeletedFalseAndStatus(dates.getRight(), status);
    }

    @Nonnull
    @Override
    public List<Tour> findByTournamentIdsAndStatus(@Nonnull final Set<Long> tournamentIds, @Nonnull final TourStatus status) {
        return repository.findByTournamentIdInAndStatusAndDeletedFalse(tournamentIds, status);
    }

    @Nonnull
    @Override
    public List<Tour> createTours(@Nonnull final Championship championship) {
        // должно уже быть задано количество туров
        if (championship.getTourCount() == null) {
            throw new LfpRuntimeException("Can't create tours for championship [" + championship + "], because tour count is null");
        }

        // создаём указанное количество туров
        final List<Tour> tours = new ArrayList<>();
        for (int i = 0; i < championship.getTourCount(); i++) {
            tours.add(create(new Tour(i + " тур", championship.getId())));
        }

        return tours;
    }

    @Nonnull
    @Override
    public Tour create(@Nonnull final Tour tour, @Nonnull final List<Match> matchList) {
        // создаём тур
        final Tour created = create(tour);

        // сохраняем список матчей
        final Pair<Date, Date> minMaxDate = saveMatchList(tour, matchList);

        // обновляем даты тура в матче
        updateTourDates(minMaxDate.getLeft(), minMaxDate.getRight(), created);

        return created;
    }

    @Nonnull
    @Override
    public Tour update(@Nonnull final Tour tour, @Nonnull final List<Match> matchList) {
        // обновляем тур
        final Tour updated = update(tour);

        // сохраняем список матчей
        final Pair<Date, Date> minMaxDate = saveMatchList(tour, matchList);

        // обновляем даты тура в матче
        updateTourDates(minMaxDate.getLeft(), minMaxDate.getRight(), updated);

        return updated;
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
                })
                // после удаления турнира, удаляем все туры в нём
                // (турнир можно удалить только на стадии конфигурации, так что дополнительных проверок не нужно)
                .doIf(Tournament.class, ChangeEvent.ChangeEventType.POST_DELETE, t -> {
                    final List<Tour> tours = findByTournamentIdAndDeletedFalse(t.getId());
                    delete(tours);
                });

        return res;
    }

    @Nonnull
    @Override
    public Set<ChangeEvent.ChangeEventType> getEventTypes() {
        return CollectionUtils.asSet(ChangeEvent.ChangeEventType.PRE_DELETE, ChangeEvent.ChangeEventType.POST_DELETE);
    }

    @Nonnull
    @Override
    public Set<Class> getEntityTypes() {
        return CollectionUtils.asSet(MatchPredict.class, Tournament.class);
    }

    /**
     * Функция для сохранения списка матчей в туре
     *
     * @param tour      тур
     * @param matchList список матчей
     * @return pair of (min date, max date)
     */
    @Nonnull
    private Pair<Date, Date> saveMatchList(@Nonnull final Tour tour, @Nonnull final List<Match> matchList) {
        // матчи, которые уже есть в базе
        final List<Match> oldMatches = matchService.findByTourIdAndDeletedFalse(tour.getId());

        // если тур уже стартовал, а список матчей почему-то изменился, то ругаемся
        if (tour.getStatus() != TourStatus.NOT_STARTED && !oldMatches.equals(matchList)) {
            throw new ValidationException(EntityValidatorResult.validatorResult("matchList", "Can't modify match list for already started tour", "tour_match_list_can_t_modify"));
        }

        // создаём список матчей
        Date maxDate = null;
        Date minDate = null;
        for (final Match match : matchList) {
            if (oldMatches.contains(match)) {
                // обновляем матч
                matchService.update(match);
            } else {
                // сохраняем id тура в матче
                match.setTourId(tour.getId());
                // создаём новый матч
                matchService.create(match);
            }

            if (match.getDate() != null && (minDate == null || match.getDate().before(minDate))) {
                minDate = match.getDate();
            }
            if (match.getDate() != null && (maxDate == null || match.getDate().after(maxDate))) {
                maxDate = match.getDate();
            }
        }

        // теперь удалим матчи, которых не было во входном списке
        oldMatches.removeAll(matchList);
        matchService.delete(oldMatches);

        return new ImmutablePair<>(minDate, maxDate);
    }

    /**
     * Обновление дат открытия, старта и закрытия тура по датам списка матчей в этом туре
     *
     * @param minDate дата первого матча
     * @param maxDate дата последнего матча
     * @param tour    тур
     */
    private void updateTourDates(@Nullable final Date minDate, @Nullable final Date maxDate, @Nonnull final Tour tour) {
        // вычисляем даты открытия, старта и завершения тура по списку матчей
        final Calendar c = Calendar.getInstance();
        if (maxDate != null && minDate != null) {
            c.setTime(minDate);
            // старт тура - дата первого матча
            tour.setStartDate(minDate);
            // открытие тура за неделю до начала первого матча
            c.add(Calendar.DAY_OF_YEAR, -7);
            tour.setOpenDate(c.getTime());
            // закрытие тура на следующий день после окончания последнего матча
            c.setTime(maxDate);
            c.add(Calendar.DAY_OF_YEAR, 1);
            tour.setFinishDate(c.getTime());
        } else {
            tour.setStartDate(null);
            tour.setFinishDate(null);
            tour.setOpenDate(null);
        }
        // тур можно не сохранять, так как это attached объект
    }
}
