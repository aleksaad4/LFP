package ad4si2.lfp.data.services.tour;

import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.data.entities.tournament.Championship;
import ad4si2.lfp.data.repositories.tour.TourRepository;
import ad4si2.lfp.utils.data.IAccountCRUDService;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface TourService extends IAccountCRUDService<Tour, Long, TourRepository> {

    /**
     * Метод для создания туров для чемпионата
     *
     * @param championship чемпионат
     * @return список созданных туров
     */
    @Nonnull
    List<Tour> createTours(@Nonnull final Championship championship);

    /**
     * Получение списка туров в турнире
     *
     * @param tId id турнира
     * @return список туров
     */
    @Nonnull
    List<Tour> findByTournamentIdAndDeletedFalse(final long tId);

    @Nonnull
    List<Tour> findByOpenDateAndStatus(@Nonnull final Date date, @Nonnull final TourStatus status);

    @Nonnull
    List<Tour> findByStartDateAndStatus(@Nonnull final Date date, @Nonnull final TourStatus status);

    @Nonnull
    List<Tour> findByFinishDateAndStatus(@Nonnull final Date date, @Nonnull final TourStatus status);

    @Nonnull
    List<Tour> findByTournamentIdsAndStatus(@Nonnull final Set<Long> tournamentIds, @Nonnull final TourStatus status);

    /**
     * Создание тура с указанным списком матчей
     *
     * @param tour      тур
     * @param matchList список матчей
     * @return созданный тур
     */
    @Nonnull
    Tour create(@Nonnull final Tour tour, @Nonnull final List<Match> matchList);

    /**
     * Обновление тура с указанным списком матчей
     *
     * @param tour      тур
     * @param matchList список матчей
     * @return обновленный тур
     */
    @Nonnull
    Tour update(@Nonnull final Tour tour, @Nonnull final List<Match> matchList);
}
