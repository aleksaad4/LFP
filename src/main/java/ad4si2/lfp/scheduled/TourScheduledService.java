package ad4si2.lfp.scheduled;

import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.data.services.tour.TourService;
import ad4si2.lfp.data.services.tournament.TournamentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Service
public class TourScheduledService {

    @Inject
    private TourService tourService;

    @Inject
    private TournamentService tournamentService;

    @Scheduled(cron = "0 * * * * *")
    public void openTours() {
        // находим все туры, у которых дата открытия 'СЕГОДНЯ' и статус 'NOT_STARTED' и переводим их в статус 'OPEN'
        final List<Tour> tours = tourService.findByOpenDateAndStatus(new Date(), TourStatus.NOT_STARTED);
        handleTours(tours, TourStatus.OPEN);
        // помним, что турнир начинается сам, с открытия первого тура
    }

    @Scheduled(cron = "0 * * * * *")
    public void startTours() {
        // находим все туры, у которых дата начала 'СЕГОДНЯ' и статус 'OPEN' и переводим их в статус 'PROGRESS'
        final List<Tour> tours = tourService.findByStartDateAndStatus(new Date(), TourStatus.OPEN);
        handleTours(tours, TourStatus.PROGRESS);
    }

    @Scheduled(cron = "0 * * * * *")
    public void finishTours() {
        // находим все туры, у которых дата завершения 'СЕГОДНЯ' и статус 'PROGRESS' и переводим их в статус 'FINISH'
        final List<Tour> tours = tourService.findByFinishDateAndStatus(new Date(), TourStatus.PROGRESS);
        handleTours(tours, TourStatus.FINISH);
    }

    /**
     * Функция для обновления статуса списку туров
     *
     * @param tours  список туров
     * @param status статус, который необходимо проставить каждому туру
     */
    private void handleTours(@Nonnull final List<Tour> tours, @Nonnull final TourStatus status) {
        for (final Tour tour : tours) {
            // достанем соответствующий турнир
            final Tournament t = tournamentService.getById(tour.getTournamentId(), false);
            // проверим, что турнир уже окончательно создан, а не находится на этапе конфигурации
            if (!t.getStatus().isConfiguration()) {
                // выставляем статус
                tour.setStatus(status);
                // обновляем тур
                tourService.update(tour);
            }
        }
    }
}
