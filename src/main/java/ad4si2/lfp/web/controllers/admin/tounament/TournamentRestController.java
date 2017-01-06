package ad4si2.lfp.web.controllers.admin.tounament;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.football.League;
import ad4si2.lfp.data.entities.tournament.*;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.data.services.football.LeagueService;
import ad4si2.lfp.data.services.tournament.TournamentService;
import ad4si2.lfp.data.services.tournament.TournamentStatusModifyResult;
import ad4si2.lfp.engine.ChampionshipEngine;
import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/admin/tournament")
public class TournamentRestController {

    @Inject
    protected AccountService accountService;

    @Inject
    protected WebUtils webUtils;

    @Inject
    private LeagueService leagueService;

    @Inject
    private TournamentService tournamentService;

    @Inject
    private ChampionshipEngine championshipEngine;

    /**
     * Функция завершения первого шага создания турнира
     * После завершения этого шага уже не может быть изменён состав участников и количество кругов
     */
    @RequestMapping(value = "/{id}/finish1Step", method = RequestMethod.GET)
    public AjaxResponse finishFirstStep(@PathVariable("id") @Nonnull final Long id) {
        return finishStep(id, tId -> tournamentService.toSetupLeagueAndTourCountStatus(tId));
    }

    /**
     * Функция завершения второго шага создания турнира
     * После завершения этого шага уже не может быть изменена выбранная лига и изменено количество туров (для чемпионата)
     */
    @RequestMapping(value = "/{id}/finish2Step", method = RequestMethod.GET)
    public AjaxResponse finishSecondStep(@PathVariable("id") @Nonnull final Long id) {
        return finishStep(id, tId -> tournamentService.toSetupTourList(tId));
    }

    @RequestMapping(value = "/types", method = RequestMethod.GET)
    public AjaxResponse types() {
        return webUtils.successResponse(new TournamentType[]{TournamentType.CHAMPIONSHIP, TournamentType.CUP});
    }

    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public AjaxResponse players() {
        return webUtils.successResponse(accountService.findPlayersWithDeletedFalse());
    }

    @RequestMapping(value = "/{id}/leagues", method = RequestMethod.GET)
    public AjaxResponse leagues(@PathVariable("id") @Nonnull final Long id) {
        final Tournament t = tournamentService.getById(id, false);
        final List<League> leagues = leagueService.findAll(false);

        if (t.getType() == TournamentType.CHAMPIONSHIP) {
            final List<TournamentPlayerLink> links = tournamentService.findTournamentPlayerLinks(id);
            championshipEngine.markEnabledLeagues((Championship) t, links.size(), leagues);
        } else if (t.getType() == TournamentType.CUP) {
            leagues.forEach(l -> l.setEnabled(true));
        }

        return webUtils.successResponse(leagues);
    }

    @RequestMapping(value = "/{id}/tourAndRoundCounts", method = RequestMethod.GET)
    public AjaxResponse tourAndRoundCounts(@PathVariable("id") @Nonnull final Long id) {
        final Tournament t = tournamentService.getById(id, false);

        // количество туров и кругов
        final List<Pair<Integer, Integer>> tourAndRoundCounts = new ArrayList<>();
        if (t.getType() == TournamentType.CHAMPIONSHIP) {
            final List<TournamentPlayerLink> links = tournamentService.findTournamentPlayerLinks(id);
            tourAndRoundCounts.addAll(championshipEngine.getTourAndRoundCounts((Championship) t, links.size()));
        }

        return webUtils.successResponse(tourAndRoundCounts);
    }

    @RequestMapping(method = RequestMethod.GET)
    public AjaxResponse list() {
        // список всех турниров
        final List<Tournament> tournaments = tournamentService.findAll(false);
        final List<TournamentDTO> result = convertToDTO(tournaments);
        return webUtils.successResponse(result);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResponse get(@PathVariable("id") @Nonnull final Long id) {
        // турнир по id
        final Tournament t = tournamentService.getById(id, false);
        final TournamentDTO dto = convertToDTO(t);
        return webUtils.successResponse(dto);
    }

    @RequestMapping(method = RequestMethod.POST)
    public AjaxResponse create(@RequestBody @Nonnull final TournamentDTO dto) {
        // турнир
        final Tournament t = convertFromDTO(dto);

        // валидация
        final EntityValidatorResult validatorResult = tournamentService.validateEntry(t, false);

        if (validatorResult.hasErrors()) {
            return webUtils.errorResponse(validatorResult);
        }

        // сохранение в БД
        final Tournament created = tournamentService.create(t, dto.getPlayers());
        final TournamentDTO createdDto = convertToDTO(created);

        return webUtils.successResponse(createdDto);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public AjaxResponse update(@RequestBody @Nonnull final TournamentDTO dto) {
        // турнир
        final Tournament t = convertFromDTO(dto);

        // валидация
        final EntityValidatorResult validatorResult = tournamentService.validateEntry(t, true);

        // а так же проверим, что состав участников не может изменяться, если турнир находится уже не в том статусе
        if (t.getStatus() != TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS) {
            final Set<Long> pIds = tournamentService.findTournamentPlayerLinks(t.getId()).stream().map(TournamentPlayerLink::getPlayerId).collect(Collectors.toSet());
            final Set<Long> newPlayerIds = dto.getPlayers().stream().map(Account::getId).collect(Collectors.toSet());
            if (!pIds.equals(newPlayerIds)) {
                validatorResult.addError(new EntityValidatorError("players", "Can't modify players list for tournament with status [" + t.getStatus() + "]", "tournament_players_can_t_modify"));
            }
        }

        if (validatorResult.hasErrors()) {
            return webUtils.errorResponse(validatorResult);
        }

        // сохранение в БД
        final Tournament updated = tournamentService.update(t, dto.getPlayers());
        final TournamentDTO updatedDto = convertToDTO(updated);

        return webUtils.successResponse(updatedDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public AjaxResponse delete(@PathVariable("id") @Nonnull final Long id) {
        final Tournament t = tournamentService.getById(id, false);

        // если турнир ещё в статусе настройки - его можно удалить
        if (t.getStatus().isConfiguration()) {
            tournamentService.delete(t);
            return webUtils.successResponse("OK");
        } else {
            // нельзя удалить турнир, который уже настроен
            return webUtils.errorResponse(EntityValidatorResult.validatorResult("Can't delete already configurated tournament", "tournament_can_t_delete_already_configurated"));
        }
    }

    /**
     * Завершение очередного шага турнира
     *
     * @param id id турнира
     * @param f  функция завершения шага
     * @return результат
     */
    @Nonnull
    private AjaxResponse finishStep(final long id, @Nonnull final Function<Long, TournamentStatusModifyResult> f) {
        // завершаем шаг
        final TournamentStatusModifyResult result = f.apply(id);

        // не удалось
        if (!result.isOk()) {
            return webUtils.errorResponse(result.getResult());
        }

        // noinspection ConstantConditions
        final TournamentDTO dto = convertToDTO(result.getT());
        return webUtils.successResponse(dto);
    }

    @Nonnull
    private Tournament convertFromDTO(@Nonnull final TournamentDTO dto) {
        final Tournament t;
        switch (dto.getType()) {
            case CHAMPIONSHIP:
                t = new Championship(dto.getId(), dto.getCreationDate() == null ? new Date() : dto.getCreationDate(),
                        dto.getName(), dto.getType(), dto.getStatus() != null ? dto.getStatus() : TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS,
                        dto.getLeague() != null ? dto.getLeague().getId() : null, dto.getRoundCount(), dto.getTourCount());

                // если выбрана лига, но ещё не задано количество кругов или количество туров - то проставим его
                if ((dto.getRoundCount() == null || dto.getTourCount() == null) && dto.getLeague() != null) {
                    // количество туров берем из лиги
                    ((Championship) t).setTourCount(dto.getLeague().getTourCount());
                    // количество кругов считаем по количеству туров и количеству игроков
                    // noinspection ConstantConditions
                    ((Championship) t).setRoundCount(championshipEngine.getRoundCount(dto.getLeague().getTourCount(), dto.getPlayers().size()));
                }

                break;
            case CUP:
                t = new Cup(dto.getId(), dto.getCreationDate() == null ? new Date() : dto.getCreationDate(),
                        dto.getName(), dto.getType(), dto.getStatus() != null ? dto.getStatus() : TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS,
                        dto.getLeague() != null ? dto.getLeague().getId() : null);
                break;
            default:
                throw new UnsupportedOperationException("Can't create tournament with unknown type [" + dto.getType() + "]");
        }
        return t;
    }

    @Nonnull
    private TournamentDTO convertToDTO(@Nonnull final Tournament t) {
        // какие аккаунты нужно подгрузить
        final Set<Long> accountIds = new HashSet<>();

        // аккаунт из турнира
        if (t.getAccountId() != null) {
            accountIds.add(t.getAccountId());
        }

        // загружаем все связки: турнир - игрок
        final List<TournamentPlayerLink> links = tournamentService.findTournamentPlayerLinks(t.getId());
        accountIds.addAll(links.stream().map(TournamentPlayerLink::getPlayerId).collect(Collectors.toSet()));

        // подготовим удобные мап-ы для заполнения дополнительных полей в dto
        final Map<Long, Account> id2account = accountService.findAllByIdIn(accountIds, true).stream().collect(Collectors.toMap(Account::getId, a -> a));

        // создадим dto
        final TournamentDTO dto = new TournamentDTO(t.getId(), t.getCreationDate(), t.getName(), t.getType(), t.getStatus(),
                t.getAccountId() != null ? id2account.get(t.getAccountId()) : null,
                t.getLeagueId() != null ? leagueService.findById(t.getLeagueId(), false) : null);
        if (t.getType() == TournamentType.CHAMPIONSHIP) {
            dto.setRoundCount(((Championship) t).getRoundCount());
            dto.setTourCount(((Championship) t).getTourCount());
        }
        dto.setPlayers(links.stream().map(l -> (Player) id2account.get(l.getPlayerId())).collect(Collectors.toList()));

        return dto;
    }

    @Nonnull
    private List<TournamentDTO> convertToDTO(@Nonnull final List<Tournament> tournaments) {
        // какие аккаунты нужно подгрузить
        final Set<Long> accountIds = new HashSet<>();
        // какие лиги нужно подгрузить
        final Set<Long> leagueIds = new HashSet<>();
        // id-шники всех турниров
        final Set<Long> tournamentIds = new HashSet<>();

        // заполним set-ы
        for (final Tournament t : tournaments) {
            tournamentIds.add(t.getId());
            if (t.getAccountId() != null) {
                accountIds.add(t.getAccountId());
            }
            if (t.getLeagueId() != null) {
                leagueIds.add(t.getLeagueId());
            }
        }

        // загружаем все связки: турнир - игрок
        final List<TournamentPlayerLink> links = tournamentService.findTournamentPlayerLinks(tournamentIds);
        // при загрузке аккаунтов - не забудем ещё всех игроков
        accountIds.addAll(links.stream().map(TournamentPlayerLink::getPlayerId).collect(Collectors.toSet()));

        // подготовим удобные мап-ы для заполнения дополнительных полей в dto
        final Map<Long, Account> id2account = accountService.findAllByIdIn(accountIds, true).stream().collect(Collectors.toMap(Account::getId, a -> a));
        final Map<Long, League> id2league = leagueService.findAllByIdIn(leagueIds, true).stream().collect(Collectors.toMap(League::getId, a -> a));
        final Map<Long, List<TournamentPlayerLink>> id2links = links.stream().collect(Collectors.groupingBy(TournamentPlayerLink::getTournamentId));

        // наконец заполним массив dto
        final List<TournamentDTO> result = new ArrayList<>();
        for (final Tournament t : tournaments) {
            final TournamentDTO dto = new TournamentDTO(t.getId(), t.getCreationDate(), t.getName(), t.getType(), t.getStatus(),
                    t.getAccountId() != null ? id2account.get(t.getAccountId()) : null,
                    t.getLeagueId() != null ? id2league.get(t.getLeagueId()) : null);
            if (t.getType() == TournamentType.CHAMPIONSHIP) {
                dto.setRoundCount(((Championship) t).getRoundCount());
                dto.setTourCount(((Championship) t).getTourCount());
            }
            // добавляем игроков, если они есть
            if (id2links.get(t.getId()) != null) {
                dto.setPlayers(id2links.get(t.getId()).stream().map(l -> (Player) id2account.get(l.getPlayerId())).collect(Collectors.toList()));
            }
            result.add(dto);
        }

        return result;
    }
}
