package ad4si2.lfp.web.controllers.admin.tounament;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.football.League;
import ad4si2.lfp.data.entities.tournament.Championship;
import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.data.entities.tournament.TournamentPlayerLink;
import ad4si2.lfp.data.entities.tournament.TournamentType;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.data.services.football.LeagueService;
import ad4si2.lfp.data.services.tournament.TournamentService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import ad4si2.lfp.utils.web.AjaxResponse;
import ad4si2.lfp.utils.web.WebUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Работа с турнирами идёт на интерфейсе в несколько шагов
 * Шаг 1:
 * status : CONFIGURATION_PLAYERS_SETTINGS
 * На этом этапе можно редактировать список участников турнира, а так же количество кругов, если это Чемпионат
 * По завершению этапа вызывается функция finishStep1
 * В ней идёт проверка, можно ли перейти к заполнению турнира содержимым с выбранным количеством участников и типом турнира
 * Стоит так же отобразить предупреждение на интерфейсе, что после завершения этого шага нельзя будет изменить состав участников турнира
 * <p>
 * Шаг 2:
 *
 */
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
        final Tournament t = new Tournament(dto.getId(), dto.getCreationDate(), dto.getName(), dto.getType());

        // валидация
        final EntityValidatorResult validatorResult = tournamentService.validateEntry(t, false);

        if (validatorResult.hasErrors()) {
            return webUtils.errorResponse(validatorResult);
        }

        // сохранение в БД
        final Tournament created = tournamentService.create(t);
        final TournamentDTO createdDto = convertToDTO(created);

        return webUtils.successResponse(createdDto);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public AjaxResponse update(@RequestBody @Nonnull final TournamentDTO dto) {
        // турнир
        final Tournament t = new Tournament(dto.getId(), dto.getCreationDate(), dto.getName(), dto.getType());

        // валидация
        final EntityValidatorResult validatorResult = tournamentService.validateEntry(t, false);
        if (validatorResult.hasErrors()) {
            return webUtils.errorResponse(validatorResult);
        }

        // сохранение в БД
        final Tournament updated = tournamentService.update(t);
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

        // подготовим удобные мап-ы для заполнения дополнительных полей в dto
        final Map<Long, Account> id2account = accountService.findAllByIdIn(accountIds, true).stream().collect(Collectors.toMap(Account::getId, a -> a));

        // создадим dto
        final TournamentDTO dto = new TournamentDTO(t.getId(), t.getCreationDate(), t.getName(), t.getType(), t.getStatus(),
                t.getAccountId() != null ? id2account.get(t.getAccountId()) : null,
                t.getLeagueId() != null ? leagueService.findById(t.getLeagueId(), false) : null);
        if (t.getType() == TournamentType.CHAMPIONSHIP) {
            dto.setRoundCount(((Championship) t).getRoundCount());
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
            }
            dto.setPlayers(id2links.get(t.getId()).stream().map(l -> (Player) id2account.get(l.getPlayerId())).collect(Collectors.toList()));
        }

        return result;
    }
}
