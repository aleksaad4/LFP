package ad4si2.lfp.web.controllers.player.forecast;

import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.data.entities.tournament.TournamentStatus;
import ad4si2.lfp.data.services.forecast.MeetingService;
import ad4si2.lfp.data.services.forecast.TourPredictService;
import ad4si2.lfp.data.services.tour.TourService;
import ad4si2.lfp.data.services.tournament.TournamentService;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;
import ad4si2.lfp.web.interceptors.AuthInterceptor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/user/predict")
public class PredictRestController {

    @Inject
    private TourService tourService;

    @Inject
    private TournamentService tournamentService;

    @Inject
    private MeetingService meetingService;

    @Inject
    private TourPredictService tourPredictService;

    @Inject
    private AuthInterceptor authInterceptor;

    @Inject
    private WebUtils webUtils;

    @RequestMapping(method = RequestMethod.GET)
    public AjaxResponse list(@Nonnull final HttpServletRequest request) {
        // получаем текущего игрока
        final Player player = (Player) authInterceptor.currentAccount(request);

        // список всех идущих турниров, в которых участвует текущий игрок
        final List<Tournament> tournaments = tournamentService.findByStatusAndPlayer(TournamentStatus.PROGRESS, player.getId());
        final Map<Long, Tournament> id2Tournament = tournaments.stream().collect(Collectors.toMap(Tournament::getId, t -> t));

        // список всех открытых туров, в которых можно сделать прогнозы
        final List<TourForPredictDTO> result = new ArrayList<>();
        final List<Tour> openTours = tourService.findByTournamentIdsAndStatus(id2Tournament.keySet(), TourStatus.OPEN);
        for (final Tour t : openTours) {
            final Meeting meeting = meetingService.findByTourIdAndPlayer(t.getId(), player.getId());
            // todo: set real is redict
            result.add(new TourForPredictDTO(id2Tournament.get(t.getTournamentId()), t, meeting, false));
        }

        return webUtils.successResponse(result);
    }

}
