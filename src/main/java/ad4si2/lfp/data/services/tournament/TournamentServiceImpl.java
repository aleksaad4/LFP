package ad4si2.lfp.data.services.tournament;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.football.League;
import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.data.entities.tournament.*;
import ad4si2.lfp.data.repositories.tournament.TournamentPlayerLinkRepository;
import ad4si2.lfp.data.repositories.tournament.TournamentRepository;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.data.services.football.LeagueService;
import ad4si2.lfp.data.services.forecast.MeetingService;
import ad4si2.lfp.data.services.tour.TourService;
import ad4si2.lfp.engine.ChampionshipEngine;
import ad4si2.lfp.engine.DrawEngine;
import ad4si2.lfp.utils.collection.CollectionUtils;
import ad4si2.lfp.utils.events.data.ChangeEvent;
import ad4si2.lfp.utils.events.data.ChangesEventDispatcher;
import ad4si2.lfp.utils.events.data.ChangesEventsListener;
import ad4si2.lfp.utils.events.web.WebEventsService;
import ad4si2.lfp.utils.exceptions.ValidationException;
import ad4si2.lfp.utils.validation.EntityValidatorError;
import ad4si2.lfp.utils.validation.EntityValidatorResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TournamentServiceImpl implements TournamentService, ChangesEventsListener {

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

    @Inject
    private ChampionshipEngine championshipEngine;

    @Inject
    private TourService tourService;

    @Inject
    private DrawEngine drawEngine;

    @Inject
    private MeetingService meetingService;

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
    public List<Tournament> findByStatusAndPlayer(@Nonnull final TournamentStatus status, final long playerId) {
        // находим все турниры в указанном статусе
        final List<Tournament> tournaments = repository.findByStatusAndDeletedFalse(status);

        // затем находим из них все турниры, в которых участвует указанный игрок
        final Set<Long> tIds = tournaments.stream().map(Tournament::getId).collect(Collectors.toSet());
        final List<TournamentPlayerLink> tp = tournamentPlayerLinkRepository.findByTournamentIdInAndPlayerId(tIds, playerId);

        // id турниров в которых, участвует указанный игрок
        final Set<Long> playerTournaments = tp.stream().map(TournamentPlayerLink::getTournamentId).collect(Collectors.toSet());
        return tournaments.stream().filter(t -> playerTournaments.contains(t.getId())).collect(Collectors.toList());
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
        // делаем апдейт так, чтобы разлетелись ивенты об обновлении
        final Tournament forUpdate = t.copy();
        forUpdate.setStatus(TournamentStatus.CONFIGURATION_TOUR_COUNT_SETTINGS);
        final Tournament updated = update(forUpdate);

        return TournamentStatusModifyResult.success(updated);
    }

    @Nonnull
    public TournamentStatusModifyResult toSetupTourList(final long tournamentId) {
        final Tournament t = getById(tournamentId, false);

        if (t.getType() == TournamentType.CHAMPIONSHIP) {
            final Championship c = (Championship) t;
            final List<TournamentPlayerLink> links = findTournamentPlayerLinks(t.getId());

            if (c.getLeagueId() != null) {
                // проверим, что выбранная лига разрешена для этого турнира
                final List<League> leagues = leagueService.findAll(false);
                championshipEngine.markEnabledLeagues(c, links.size(), leagues);
                final boolean leagueIsOk = leagues.stream().anyMatch(l -> l.getEnabled() != null && l.getEnabled() && Objects.equals(l.getId(), c.getLeagueId()));
                if (!leagueIsOk) {
                    return TournamentStatusModifyResult.error(EntityValidatorResult.validatorResult("league",
                            "Incorrect league [" + t.getLeagueId() + "]", "tournament.league_incorrect"));
                }
            } else if (c.getTourCount() == null) {
                // лига не выбрана, количество туров не задано и как собрались переходить к следующему шагу?
                return TournamentStatusModifyResult.error(EntityValidatorResult.validatorResult("League and tour count is absent", "tournament.league_or_tour_is_empty"));
            } else {
                // проверим, что выбрано правильное количество туров
                final boolean tourIsOk = championshipEngine.getTourAndRoundCounts(c, links.size()).stream().anyMatch(p -> Objects.equals(c.getTourCount(), p.getKey()));
                if (!tourIsOk) {
                    {
                        return TournamentStatusModifyResult.error(EntityValidatorResult.validatorResult("tourCount",
                                "Incorrect tour count [" + c.getTourCount() + "]", "tournament.tour_count_incorrect"));
                    }
                }
            }

            // если количество кругов не задано - то зададим его
            if (c.getRoundCount() == null) {
                // noinspection ConstantConditions
                c.setRoundCount(championshipEngine.getRoundCount(c.getTourCount(), links.size()));
            }
        }

        // если всё хорошо, то изменяем статус турнира на 'CONFIGURATION_TOUR_COUNT_SETTINGS'
        // делаем апдейт так, чтобы разлетелись ивенты об обновлении
        final Tournament forUpdate = t.copy();
        forUpdate.setStatus(TournamentStatus.CONFIGURATION_TOUR_LIST_SETTINGS);
        final Tournament updated = update(forUpdate);

        // если это чемпионат, то создаём туры и генерируем встречи между игроками
        if (t.getType() == TournamentType.CHAMPIONSHIP) {
            final Championship c = (Championship) t;
            // создаём туры
            final List<Tour> tours = tourService.createTours(c);
            // получим список игроков
            final List<Long> playerIds = findTournamentPlayerLinks(c.getId()).stream().map(TournamentPlayerLink::getPlayerId).collect(Collectors.toList());
            // вызываем функцию жеребьёвки, которая сгенерируем список встреч
            final List<Meeting> meetings = drawEngine.drawPlayers(c, playerIds, tours);
            // создаём встречи
            meetingService.create(meetings);
        }

        return TournamentStatusModifyResult.success(updated);
    }

    @Override
    @Nonnull
    public TournamentStatusModifyResult finishCreateTournament(final long tournamentId) {
        final Tournament t = getById(tournamentId, false);

        // проверим, что даты начала всех туров в турнире не ранее чем 'ЗАВТРА', чтобы все успели сделать прогнозы
        final List<Tour> tours = tourService.findByTournamentIdAndDeletedFalse(t.getId());
        final Calendar c = Calendar.getInstance();
        final Date now = new Date();
        for (final Tour tour : tours) {
            if (tour.getStartDate() != null) {
                c.setTime(tour.getStartDate());
                c.add(Calendar.DAY_OF_YEAR, -1);
                if (c.getTime().before(now)) {
                    return TournamentStatusModifyResult.error(EntityValidatorResult.validatorResult("Incorrect tour [" + tour.getId() + " date]",
                            "tournament.tour_dates_incorrect"));
                }
            }
        }

        // если всё хорошо, то изменяем статус турнира на 'CREATED'
        // делаем апдейт так, чтобы разлетелись ивенты об обновлении
        final Tournament forUpdate = t.copy();
        forUpdate.setStatus(TournamentStatus.CREATED);
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

    @Override
    public void delete(@Nonnull final Tournament t) {
        // нельзя удалить турнир, который уже не находится на стадии настройки
        if (!t.getStatus().isConfiguration()) {
            throw new ValidationException(EntityValidatorResult.validatorResult("Can't delete tour", "common.can_t_delete"));
        }

        TournamentService.super.delete(t);
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
        final EntityValidatorResult modifyTypeResult = new EntityValidatorResult().checkNotModify("type", entry, findById(entry.getId(), false), Tournament::getType);
        result.addErrors(modifyTypeResult.getErrors());

        // отдельные проверки для турнира с типом `ЧЕМПИОНАТ`
        if (!modifyTypeResult.hasErrors() && entry.getType() == TournamentType.CHAMPIONSHIP) {
            // если количество кругов задано, оно должно быть больше 0
            result.checkPositiveValue("roundCount", (Championship) entry, Championship::getRoundCount);
            // если количество туров задано, оно должно быть больше 0
            result.checkPositiveValue("tourCount", (Championship) entry, Championship::getTourCount);

            // обновлять количество кругов можно только на этапе настройки состава участников
            // или на этапе привязке к лиге и выборе количества туров
            if (entry.getStatus() != TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS && entry.getStatus() != TournamentStatus.CONFIGURATION_TOUR_COUNT_SETTINGS) {
                result.checkNotModify("roundCount", (Championship) entry, (Championship) findById(entry.getId(), false), Championship::getRoundCount);
            }

            // обновлять количество туров можно только на этапе выбора количества туров
            if (entry.getStatus() != TournamentStatus.CONFIGURATION_TOUR_COUNT_SETTINGS) {
                result.checkNotModify("tourCount", (Championship) entry, (Championship) findById(entry.getId(), false), Championship::getTourCount);
            }
        }

        if (entry.getStatus() != TournamentStatus.CONFIGURATION_TOUR_COUNT_SETTINGS) {
            // если этап не для выбора количества туров, то уже нельзя изменять лигу
            result.checkNotModify("league", entry, findById(entry.getId(), false), Tournament::getLeagueId);
        }

        return result;
    }

    @Nonnull
    @Override
    public EntityValidatorResult onEvent(@Nonnull final ChangeEvent event) {
        final EntityValidatorResult res = new EntityValidatorResult();

        event
                // нельзя удалить игрока, если он уже участвовал в каком-то турнире
                .doIf(Player.class, ChangeEvent.ChangeEventType.PRE_DELETE, p -> {
                    // находим все связи турнир-игрок
                    final List<TournamentPlayerLink> links = tournamentPlayerLinkRepository.findByPlayerId(p.getId());
                    // находим все неудалённые турниры, где есть удаляемый игрок
                    final List<Tournament> tournaments = findAllByIdIn(links.stream().map(TournamentPlayerLink::getTournamentId).collect(Collectors.toSet()), false);
                    if (!tournaments.isEmpty()) {
                        // если такие нашлись - то удалять игрока уже нельзя
                        res.addError(new EntityValidatorError("Can't delete player", "common.can_t_delete"));
                    }
                })
                // нельзя заблокировать игрока, если он участвует в незавершенном турнире
                .doIf(Player.class, ChangeEvent.ChangeEventType.PRE_UPDATE, (p, oldP) -> {
                    // операция блокировки игрока
                    if (p.isBlocked() && !oldP.isBlocked()) {
                        // находим все связи турнир-игрок
                        final List<TournamentPlayerLink> links = tournamentPlayerLinkRepository.findByPlayerId(p.getId());
                        // находим все неудалённые турниры, где есть редактируемый игрок
                        // считаем количество незавершенных турниров
                        final long unfinishTournamentCount = findAllByIdIn(links.stream().map(TournamentPlayerLink::getTournamentId).collect(Collectors.toSet()), false)
                                .stream().filter(t -> t.getStatus() != TournamentStatus.FINISH).count();
                        if (unfinishTournamentCount != 0) {
                            // если такие нашлись - то блокировать игрока нельзя
                            res.addError(new EntityValidatorError("blocked", "Can't block player", "common.player_can_t_block"));
                        }
                    }
                })
                // нельзя удалить лигу, если уже есть турнир по этой лиге
                .doIf(League.class, ChangeEvent.ChangeEventType.PRE_DELETE, dcc(l -> repository.findByLeagueIdAndDeletedFalse(l), res))
                // нельзя удалить тур, если турнир чемпионат и настройка турнира уже завершена
                .doIf(Tour.class, ChangeEvent.ChangeEventType.PRE_DELETE, t -> {
                    final Tournament tournament = getById(t.getTournamentId(), false);
                    if (tournament.getType() == TournamentType.CHAMPIONSHIP && !tournament.getStatus().isConfiguration()) {
                        res.addError(new EntityValidatorError("Can't delete [" + t + "]", "common.can_t_delete"));
                    }
                })
                .doIf(Tour.class, ChangeEvent.ChangeEventType.PRE_UPDATE, (t, oldT) -> {
                    // если происходит открытие тура, а турнир не находится в статусе 'PROGRESS', то переводим его в этот статус
                    if (oldT.getStatus() != TourStatus.OPEN && t.getStatus() == TourStatus.OPEN) {
                        final Tournament tournament = getById(t.getTournamentId(), false);
                        if (tournament.getStatus() != TournamentStatus.PROGRESS) {
                            // начинаем турнир
                            final Tournament forUpdate = tournament.copy();
                            forUpdate.setStatus(TournamentStatus.PROGRESS);
                            update(forUpdate);
                        }
                    }
                });

        return res;
    }

    @Nonnull
    @Override
    public Set<ChangeEvent.ChangeEventType> getEventTypes() {
        return CollectionUtils.asSet(ChangeEvent.ChangeEventType.PRE_DELETE, ChangeEvent.ChangeEventType.PRE_UPDATE);
    }

    @Nonnull
    @Override
    public Set<Class> getEntityTypes() {
        return CollectionUtils.asSet(Player.class, League.class, Tour.class);
    }

}
