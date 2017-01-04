package ad4si2.lfp.data.services.tournament;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.tournament.*;
import ad4si2.lfp.data.repositories.tournament.TournamentPlayerLinkRepository;
import ad4si2.lfp.data.repositories.tournament.TournamentRepository;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.data.services.football.LeagueService;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TournamentServiceImpl implements TournamentService {

    @Inject
    private TournamentRepository repository;

    @Inject
    private TournamentPlayerLinkRepository tournamentPlayerLinkRepository;

    @Inject
    private ChangesEventDispatcher eventDispatcher;

    @Inject
    private WebEventsService webEventsService;

    @Inject
    private LeagueService leagueService;

    @Inject
    private AccountService accountService;

    @Nonnull
    @Override
    public TournamentRepository getRepo() {
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
    public List<TournamentPlayerLink> findTournamentPlayerLinks(final long tournamentId) {
        return tournamentPlayerLinkRepository.findByTournamentId(tournamentId);
    }

    @Nonnull
    @Override
    public List<TournamentPlayerLink> findTournamentPlayerLinks(@Nonnull final Set<Long> tournamentIds) {
        return tournamentIds.isEmpty() ? new ArrayList<>() : tournamentPlayerLinkRepository.findByTournamentIdIn(tournamentIds);
    }

    @Nonnull
    @Override
    public TournamentStatusModifyResult toSetupLeagueAndTourCountStatus(final long tournamentId) {
        // проверим, что все добавленные игроки активны
        final Set<Long> playerIds = findTournamentPlayerLinks(tournamentId).stream().map(TournamentPlayerLink::getPlayerId).collect(Collectors.toSet());
        final List<Account> players = accountService.findAllByIdIn(playerIds, false).stream().filter(p -> !p.isBlocked()).collect(Collectors.toList());
        if (playerIds.size() != players.size()) {
            return TournamentStatusModifyResult.error(EntityValidatorResult.validatorResult("players", "Player list contains blocked players", "tournament.players_incorrect"));
        }

        // получим турнир
        final Tournament t = getById(tournamentId, false);

        // проверим, что для кубка количество игроков минимум 1, а для чемпионата их минимум 2
        if (t.getType().getMinPlayersCount() > playerIds.size()) {
            return TournamentStatusModifyResult.error(EntityValidatorResult.validatorResult("players", "Player list min size [" + t.getType().getMinPlayersCount() + "]", "tournament.players_min_size"));
        }

        // если всё хорошо, то изменяем статус турнира на 'CONFIGURATION_TOUR_COUNT_SETTINGS'
        final Tournament forUpdate = t.copy();
        forUpdate.setStatus(TournamentStatus.CONFIGURATION_TOUR_COUNT_SETTINGS);
        final Tournament updated = update(forUpdate);

        return TournamentStatusModifyResult.success(updated);
    }

    @Nonnull
    public TournamentStatusModifyResult toSetupTourList(final long tournamentId) {
        final Tournament t = getById(tournamentId, false);

        if (t.getType() == TournamentType.CHAMPIONSHIP) {
            // todo: проверим, что выбранная лига разрешена для этого турнира
            // todo: или проверим, что выбранное количество туров разрешено для этого турнира
            // todo: сохранить количество кругов в таком случае
        }

        // если всё хорошо, то изменяем статус турнира на 'CONFIGURATION_TOUR_COUNT_SETTINGS'
        final Tournament forUpdate = t.copy();
        forUpdate.setStatus(TournamentStatus.CONFIGURATION_TOUR_LIST_SETTINGS);
        final Tournament updated = update(forUpdate);

        return TournamentStatusModifyResult.success(updated);
    }

    @Nonnull
    @Override
    public Tournament create(@Nonnull final Tournament tournament, @Nonnull final List<Player> players) {
        // создадим турнир
        final Tournament created = create(tournament);

        // привяжем к нему игроков
        for (final Player player : players) {
            tournamentPlayerLinkRepository.save(new TournamentPlayerLink(player.getId(), created.getId()));
        }

        return created;
    }

    @Nonnull
    @Override
    public Tournament update(@Nonnull final Tournament tournament, @Nonnull final List<Player> players) {
        // обновим турнир
        final Tournament updated = update(tournament);

        // обновим список игроков
        final List<TournamentPlayerLink> links = tournamentPlayerLinkRepository.findByTournamentId(updated.getId());
        final Set<Long> pIds = players.stream().map(Account::getId).collect(Collectors.toSet());

        // удалим сначала игроков, которых нет в новом наборе
        for (final TournamentPlayerLink link : links) {
            if (!pIds.contains(link.getPlayerId())) {
                tournamentPlayerLinkRepository.delete(link);
            }
        }

        // теперь добавим новых игроков
        final Set<Long> linkPlayerIds = links.stream().map(TournamentPlayerLink::getPlayerId).collect(Collectors.toSet());
        for (final Long pId : pIds) {
            if (!linkPlayerIds.contains(pId)) {
                tournamentPlayerLinkRepository.save(new TournamentPlayerLink(pId, updated.getId()));
            }
        }

        return updated;
    }

    @Nonnull
    @Override
    public EntityValidatorResult validateEntry(final Tournament entry, final boolean forUpdate) {
        // стандартные проверки
        final EntityValidatorResult result = TournamentService.super.validateEntry(entry, forUpdate);

        // проверки на правильность заполнения полей
        result.checkIsEmpty("name", entry, Tournament::getName)
                .checkMaxSize("name", 256, entry, Tournament::getName)
                .checkIsNull("type", entry, Tournament::getType);

        // проверки на уникальность
        result.checkDuplicate("name", entry, Tournament::getName, name -> repository.findByNameAndDeletedFalse(name), forUpdate);

        // проверка, что связанные объекты существуют
        result.checkLinkedValue("leagueId", entry, entry.getLeagueId(), (linkedId) -> leagueService.findById(linkedId, false), false);

        // нельзя изменять тип турнира
        result.checkNotModify("type", entry, findById(entry.getId(), false), Tournament::getType);

        // отдельные проверки для турнира с типом `ЧЕМПИОНАТ`
        if (entry.getType() == TournamentType.CHAMPIONSHIP) {
            // если количество кругов задано, оно должно быть больше 0
            result.checkPositiveValue("roundCount", (Championship) entry, Championship::getRoundCount);

            // обновлять количество кругов можно только на этапе настройки состава участников
            // или на этапе привязке к лиге и выборе количества туров
            if (entry.getStatus() != TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS && entry.getStatus() != TournamentStatus.CONFIGURATION_TOUR_COUNT_SETTINGS) {
                result.checkNotModify("roundCount", (Championship) entry, (Championship) findById(entry.getId(), false), Championship::getRoundCount);
            }
        }

        if (entry.getStatus() != TournamentStatus.CONFIGURATION_TOUR_COUNT_SETTINGS) {
            // если этап не для выбора количества туров, то уже нельзя изменять лигу
            result.checkNotModify("league", entry, findById(entry.getId(), false), Tournament::getLeagueId);
        }

        return result;
    }

}
