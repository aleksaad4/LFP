package ad4si2.lfp.web.controllers.admin.tour;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.entities.football.Team;
import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.data.entities.tournament.TournamentType;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.data.services.football.MatchService;
import ad4si2.lfp.data.services.football.TeamService;
import ad4si2.lfp.data.services.forecast.MeetingService;
import ad4si2.lfp.data.services.tour.TourService;
import ad4si2.lfp.data.services.tournament.TournamentService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/admin/tour")
public class TourRestController {

    @Inject
    protected AccountService accountService;

    @Inject
    protected WebUtils webUtils;

    @Inject
    private TournamentService tournamentService;

    @Inject
    private TourService tourService;

    @Inject
    private MatchService matchService;

    @Inject
    private MeetingService meetingService;

    @Inject
    private TeamService teamService;

    @RequestMapping(value = "{tId}/tournament", method = RequestMethod.GET)
    public AjaxResponse tournament(@PathVariable("tId") @Nonnull final Long tId) {
        // турнир
        final Tournament t = tournamentService.getById(tId, false);
        return webUtils.successResponse(t);
    }

    @RequestMapping(value = "{tId}/players", method = RequestMethod.GET)
    public AjaxResponse players(@PathVariable("tId") @Nonnull final Long tId) {
        // список игроков
        final List<Account> players = accountService.findAll(false);
        return webUtils.successResponse(players);
    }

    @RequestMapping(value = "{tId}/statuses", method = RequestMethod.GET)
    public AjaxResponse statuses(@PathVariable("tId") @Nonnull final Long tId) {
        return webUtils.successResponse(TourStatus.values());
    }

    @RequestMapping(value = "{tId}/teams", method = RequestMethod.GET)
    public AjaxResponse teams(@PathVariable("tId") @Nonnull final Long tId) {
        // список команд
        final List<Team> teams = teamService.findAll(false);
        return webUtils.successResponse(teams);
    }

    @RequestMapping(value = "/{tId}", method = RequestMethod.GET)
    public AjaxResponse list(@PathVariable("tId") @Nonnull final Long tId) {
        // список всех туров
        final List<Tour> tours = tourService.findByTournamentIdAndDeletedFalse(tId);
        final List<TourDTO> dtos = convertToDTO(tours);
        return webUtils.successResponse(dtos);
    }

    @RequestMapping(value = "/{tId}/{id}", method = RequestMethod.GET)
    public AjaxResponse get(@PathVariable("tId") @Nonnull final Long tId, @PathVariable("id") @Nonnull final Long id) {
        // тур по id
        final Tour t = tourService.getById(id, false);
        final TourDTO dto = convertToDTO(t);
        return webUtils.successResponse(dto);
    }

    @RequestMapping(value = "/{tId}", method = RequestMethod.POST)
    public AjaxResponse create(@RequestBody @Nonnull final TourDTO dto) {
        // тур
        final Tour t = convertFromDTO(dto);

        // валидация
        final EntityValidatorResult validatorResult = tourService.validateEntry(t, false);

        if (validatorResult.hasErrors()) {
            return webUtils.errorResponse(validatorResult);
        }

        // сохранение в БД
        final Tour created = tourService.create(t, dto.getMatchList());
        final TourDTO createdDto = convertToDTO(created);

        return webUtils.successResponse(createdDto);
    }

    @RequestMapping(value = "/{tId}", method = RequestMethod.PATCH)
    public AjaxResponse update(@RequestBody @Nonnull final TourDTO dto) {
        // тур
        final Tour t = convertFromDTO(dto);

        // валидация
        final EntityValidatorResult validatorResult = tourService.validateEntry(t, true);
        if (validatorResult.hasErrors()) {
            return webUtils.errorResponse(validatorResult);
        }

        // сохранение в БД
        final Tour updated = tourService.update(t, dto.getMatchList());
        final TourDTO updatedDto = convertToDTO(updated);

        return webUtils.successResponse(updatedDto);
    }

    @RequestMapping(value = "/{tId}/{id}", method = RequestMethod.DELETE)
    public AjaxResponse delete(@PathVariable("tId") @Nonnull final Long tId, @PathVariable("id") @Nonnull final Long id) {
        final Tour tour = tourService.getById(id, false);
        final Tournament tournament = tournamentService.getById(tId, false);

        // если тур ещё не не стартовал и турнир кубок, то его можно удалить
        if (tour.getStatus() == TourStatus.NOT_STARTED && tournament.getType() == TournamentType.CUP) {
            tourService.delete(tour);
            return webUtils.successResponse("OK");
        } else {
            // в другом случае удалить нельзя
            return webUtils.errorResponse(EntityValidatorResult.validatorResult("Can't delete tour", "common.can_t_delete"));
        }
    }

    @Nonnull
    private Tour convertFromDTO(@Nonnull final TourDTO dto) {
        return new Tour(dto.getId(), dto.getName(), dto.getTournamentId(), dto.getOpenDate(),
                dto.getStartDate(), dto.getFinishDate(), dto.getStatus() != null ? dto.getStatus() : TourStatus.NOT_STARTED);
    }

    @Nonnull
    private TourDTO convertToDTO(@Nonnull final Tour t) {
        // создадим dto
        final Tournament tournament = tournamentService.getById(t.getTournamentId(), false);
        final TourDTO dto = new TourDTO(t.getId(), t.getName(), t.getAccountId() != null ? accountService.findById(t.getId(), true) : null,
                t.getTournamentId(), t.getStatus(), t.getOpenDate(), t.getStartDate(), t.getFinishDate());

        // проставляем список матчей
        dto.setMatchList(matchService.findByTourIdAndDeletedFalse(t.getId()));
        // проставляем список встреч для турнира типа ЧЕМПИОНАТ
        if (tournament.getType() == TournamentType.CHAMPIONSHIP) {
            dto.setMeetingList(meetingService.findByTourIdAndDeletedFalse(t.getId()));
        }

        return dto;
    }

    @Nonnull
    private List<TourDTO> convertToDTO(@Nonnull final List<Tour> tours) {
        // список аккаунтов, которые нужно подгрузить
        final Set<Long> accountIds = new HashSet<>();
        // список id туров
        final Set<Long> tourIds = new HashSet<>();
        // список id турнирова
        final Set<Long> tournamentIds = new HashSet<>();

        // заполним списки
        for (final Tour tour : tours) {
            if (tour.getAccountId() != null) {
                accountIds.add(tour.getAccountId());
            }
            tournamentIds.add(tour.getTournamentId());
            tourIds.add(tour.getId());
        }

        // загрузим аккаунты, матчи, встречи и турниры
        final Map<Long, Account> id2account = accountService.findAllByIdIn(accountIds, true).stream().collect(Collectors.toMap(Account::getId, a -> a));
        final Map<Long, List<Match>> tour2match = matchService.findByTourIdInAndDeletedFalse(tourIds).stream().collect(Collectors.groupingBy(Match::getTourId));
        final Map<Long, List<Meeting>> tour2meeting = meetingService.findByTourIdInAndDeletedFalse(tourIds).stream().collect(Collectors.groupingBy(Meeting::getTourId));
        final Map<Long, Tournament> id2tournament = tournamentService.findAllByIdIn(tournamentIds, false).stream().collect(Collectors.toMap(t -> t.getId(), t -> t));

        // наконец заполним массив dto
        final List<TourDTO> result = new ArrayList<>();
        for (final Tour tour : tours) {
            final Tournament tournament = id2tournament.get(tour.getTournamentId());
            final TourDTO dto = new TourDTO(tour.getId(), tour.getName(), tour.getAccountId() != null ? id2account.get(tour.getAccountId()) : null,
                    tour.getTournamentId(), tour.getStatus(), tour.getOpenDate(), tour.getStartDate(), tour.getFinishDate());
            // проставляем список матчей
            dto.setMatchList(tour2match.get(tour.getId()));
            // проставляем список встреч для турнира типа ЧЕМПИОНАТ
            if (tournament.getType() == TournamentType.CHAMPIONSHIP) {
                dto.setMeetingList(tour2meeting.get(tour.getId()));
            }
            result.add(dto);
        }

        return result;
    }
}
