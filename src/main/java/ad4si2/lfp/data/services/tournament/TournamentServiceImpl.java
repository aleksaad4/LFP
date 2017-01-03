package ad4si2.lfp.data.services.tournament;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.tournament.*;
import ad4si2.lfp.data.repositories.tournament.TournamentPlayerLinkRepository;
import ad4si2.lfp.data.repositories.tournament.TournamentRepository;
import ad4si2.lfp.data.services.football.LeagueService;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;
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
            if (!pIds.contains(link.getId())) {
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
            result.checkPositiveValue("tourCount", (Championship) entry, Championship::getRoundCount);

            // обновлять количество кругов можно только на этапе настройки состава участников
            if (entry.getStatus() != TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS) {
                result.checkNotModify("tourCount", (Championship) entry, (Championship) findById(entry.getId(), false), Championship::getRoundCount);
            }
        }

        return result;
    }
}
