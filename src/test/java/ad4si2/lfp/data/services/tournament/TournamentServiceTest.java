package ad4si2.lfp.data.services.tournament;

import ad4si2.lfp.BaseTest;
import ad4si2.lfp.TestUtils;
import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.football.League;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tournament.*;
import ad4si2.lfp.data.services.account.AccountService;
import ad4si2.lfp.data.services.account.AccountServiceTest;
import ad4si2.lfp.data.services.football.CountryService;
import ad4si2.lfp.data.services.football.LeagueService;
import ad4si2.lfp.data.services.football.LeagueServiceTest;
import ad4si2.lfp.data.services.football.TeamService;
import ad4si2.lfp.data.services.forecast.MeetingService;
import ad4si2.lfp.data.services.tour.TourService;
import ad4si2.lfp.utils.collection.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TournamentServiceTest extends BaseTest {

    @Inject
    private CountryService countryService;

    @Inject
    private TeamService teamService;

    @Inject
    private LeagueService leagueService;

    @Inject
    private TournamentService tournamentService;

    @Inject
    private AccountService accountService;

    @Inject
    private TourService tourService;

    @Inject
    private MeetingService meetingService;

    @Test
    public void validationTest() {
        // ошибка при пустом названии турнира
        TestUtils.validateCreateFailTest(tournamentService, new Cup("", null));

        final String tText = "tournament";
        final Tournament t = tournamentService.create(new Cup(tText, null));

        // ошибка при создании турнира с таким же названием, как уже есть
        TestUtils.validateDuplicateTest(tournamentService, t, new Cup(tText, null));

        // ошибка при указании несуществующей лиги в качестве страны команды
        TestUtils.validateCreateFailTest(tournamentService, new Cup("t1", 1L));

        // нельзя обновлять тип уже существующего турнира
        t.setType(TournamentType.CHAMPIONSHIP);
        TestUtils.validateUpdateFailTest(tournamentService, t);

        // ошибка при не положительном количество кругов или туров
        TestUtils.validateCreateFailTest(tournamentService, new Championship("t1", null, null, 0));
        TestUtils.validateCreateFailTest(tournamentService, new Championship("t1", null, null, -1));
        TestUtils.validateCreateFailTest(tournamentService, new Championship("t1", null, 0, null));
        TestUtils.validateCreateFailTest(tournamentService, new Championship("t1", null, -1, null));

        // ошибка при попытки изменить количество кругов у турнира в неподходящем статусе
        final Championship c = (Championship) tournamentService.create(new Championship("t1", null, 1, 1));
        Arrays.asList(TournamentStatus.CONFIGURATION_TOUR_LIST_SETTINGS, TournamentStatus.CREATED, TournamentStatus.PROGRESS, TournamentStatus.FINISH).forEach(s -> {
            c.setStatus(s);
            c.setRoundCount(2);
            TestUtils.validateUpdateFailTest(tournamentService, c);
        });
        // вернем round count назад
        c.setRoundCount(1);

        // ошибка при попытки изменить количество туров у турнира в неподходящем статусе
        Arrays.asList(TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS, TournamentStatus.CONFIGURATION_TOUR_LIST_SETTINGS, TournamentStatus.CREATED, TournamentStatus.PROGRESS, TournamentStatus.FINISH).forEach(s -> {
            c.setStatus(s);
            c.setTourCount(2);
            TestUtils.validateUpdateFailTest(tournamentService, c);
        });
        // вернем tour count назад
        c.setTourCount(1);

        // ошибка при попытки изменить лигу
        final League league = leagueService.create(LeagueServiceTest.createLeague("league"));
        Arrays.asList(TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS, TournamentStatus.CONFIGURATION_TOUR_LIST_SETTINGS, TournamentStatus.CREATED, TournamentStatus.PROGRESS, TournamentStatus.FINISH).forEach(s -> {
            c.setStatus(s);
            c.setLeagueId(league.getId());
            TestUtils.validateUpdateFailTest(tournamentService, c);
        });
        // вернем указанную лигу обратно
        c.setLeagueId(null);
    }

    @Test
    public void serviceTest() {
        final Player p1 = (Player) accountService.create(AccountServiceTest.createPlayer("p1"));
        final Player p2 = (Player) accountService.create(AccountServiceTest.createPlayer("p2"));
        final Player p3 = (Player) accountService.create(AccountServiceTest.createPlayer("p3"));
        final Player p4 = (Player) accountService.create(AccountServiceTest.createPlayer("p4"));

        // созданием кубка
        final Tournament c = tournamentService.create(new Cup("cup", null), Arrays.asList(p1, p2));
        Assert.assertEquals(c.getType(), TournamentType.CUP);
        Assert.assertTrue(c instanceof Cup);
        final List<TournamentPlayerLink> tp = tournamentService.findTournamentPlayerLinks(c.getId());
        Assert.assertEquals(tp.size(), 2);
        Assert.assertEquals(tp.stream().filter(p -> p.getPlayerId() == p1.getId()).count(), 1);
        Assert.assertEquals(tp.stream().filter(p -> p.getPlayerId() == p2.getId()).count(), 1);

        // создание чемпионата
        final Tournament ch = tournamentService.create(new Championship("champ", null, null, null), Arrays.asList(p1, p2));
        Assert.assertEquals(ch.getType(), TournamentType.CHAMPIONSHIP);
        Assert.assertTrue(ch instanceof Championship);
        Assert.assertEquals(tournamentService.findTournamentPlayerLinks(c.getId()).size(), 2);

        // изменение состава участников турнира
        tournamentService.update(c, Arrays.asList(p2, p3, p4));
        final List<TournamentPlayerLink> tp2 = tournamentService.findTournamentPlayerLinks(c.getId());
        Assert.assertEquals(tp2.size(), 3);
        Assert.assertEquals(tp2.stream().filter(p -> p.getPlayerId() == p2.getId()).count(), 1);
        Assert.assertEquals(tp2.stream().filter(p -> p.getPlayerId() == p3.getId()).count(), 1);
        Assert.assertEquals(tp2.stream().filter(p -> p.getPlayerId() == p4.getId()).count(), 1);

        // проверка, что нельзя удалить турнир, который более не находится в статусе конфигурации
        Arrays.asList(TournamentStatus.CREATED, TournamentStatus.PROGRESS, TournamentStatus.FINISH).forEach(s -> {
            c.setStatus(s);
            tournamentService.update(c);
            TestUtils.catchExceptionTest(() -> tournamentService.delete(c));
        });

        // методы поиска
        // c - p2, p3, p4, ch - p1, p2
        Assert.assertEquals(tournamentService.findTournamentPlayerLinks(CollectionUtils.asSet(c.getId(), ch.getId())).size(), 5);
    }

    @Test
    public void eventListenerTest() {
        // создадим всё, что нужно
        final Player player = (Player) accountService.create(AccountServiceTest.createPlayer("player"));
        final League league = leagueService.create(LeagueServiceTest.createLeague("league"));
        final Tournament c = tournamentService.create(new Cup("cup", null), Collections.singletonList(player));

        // нельзя удалить игрока, который участвовал уже в каком-либо турнире
        TestUtils.catchExceptionTest(() -> accountService.delete(player));

        // нельзя заблокировать игрока, который участвует в незавершенном турнире
        player.setBlocked(true);
        TestUtils.catchExceptionTest(() -> accountService.update(player));
        // а если турнир завершен, то уже можно
        c.setStatus(TournamentStatus.FINISH);
        tournamentService.update(c);
        accountService.update(player);

        final Tournament c1 = tournamentService.create(new Cup("cup1", league.getId()));
        // нельзя удалить лигу, если есть турнир по этой лиге
        TestUtils.catchExceptionTest(() -> leagueService.delete(league));
        // а если турнир уже удален, то можно
        tournamentService.delete(c1);
        leagueService.delete(league);

        // нельзя удалить тур из чемпионата, который уже не находится в стадии настройки
        final Tournament ch = tournamentService.create(new Championship("ch", null, null, null));
        final Tour t = tourService.create(new Tour("tour", ch.getId()));
        ch.setStatus(TournamentStatus.CREATED);
        tournamentService.update(ch);
        TestUtils.catchExceptionTest(() -> tourService.delete(t));
        // а в другом состоянии можно
        ch.setStatus(TournamentStatus.CONFIGURATION_TOUR_LIST_SETTINGS);
        tournamentService.update(ch);
        tourService.delete(t);
    }

    @Test
    public void toSetupLeagueAndTourCountStatusTest() {
        // тестируем завершение первого этапа настройки турнира

        // игроки
        final Player p1 = (Player) accountService.create(AccountServiceTest.createPlayer("p1"));
        final Player p4 = (Player) accountService.create(AccountServiceTest.createPlayer("p4"));

        // удаленный игрок
        final Player deletedP = (Player) accountService.create(AccountServiceTest.createPlayer("p2"));
        accountService.delete(deletedP);

        // заблокированный игрок
        final Player blockedP = (Player) accountService.create(AccountServiceTest.createPlayer("p3"));
        blockedP.setBlocked(true);
        accountService.update(blockedP);

        // все участники должны быть не удаленные и не заблокированные
        // есть удаленный участник
        final Tournament t = tournamentService.create(new Cup("cup", null), Arrays.asList(p1, deletedP));
        Assert.assertFalse(tournamentService.toSetupLeagueAndTourCountStatus(t.getId()).isOk());
        // есть заблокированный участник
        tournamentService.update(t, Arrays.asList(p1, blockedP));
        Assert.assertFalse(tournamentService.toSetupLeagueAndTourCountStatus(t.getId()).isOk());

        // кубок
        // должно быть не менее 1-ого участника
        tournamentService.update(t, new ArrayList<>());
        Assert.assertFalse(tournamentService.toSetupLeagueAndTourCountStatus(t.getId()).isOk());

        // ну и наконец, всё хорошо
        tournamentService.update(t, Collections.singletonList(p1));
        final TournamentStatusModifyResult tsmr = tournamentService.toSetupLeagueAndTourCountStatus(t.getId());
        Assert.assertTrue(tsmr.isOk());
        Assert.assertNotNull(tsmr.getT());
        // и у турнира сменился статус
        Assert.assertEquals(tsmr.getT().getStatus(), TournamentStatus.CONFIGURATION_TOUR_COUNT_SETTINGS);

        // чемпионат
        // должно быть не менее 2-ух участников
        // 0 - не подходит
        final Tournament ch = tournamentService.create(new Championship("ch", null, null, null), new ArrayList<>());
        Assert.assertFalse(tournamentService.toSetupLeagueAndTourCountStatus(ch.getId()).isOk());
        // 1 - тоже не устраивает
        tournamentService.update(ch, Collections.singletonList(p1));
        Assert.assertFalse(tournamentService.toSetupLeagueAndTourCountStatus(ch.getId()).isOk());
        // а вот 2 - уже достаочно
        tournamentService.update(ch, Arrays.asList(p1, p4));
        Assert.assertTrue(tournamentService.toSetupLeagueAndTourCountStatus(ch.getId()).isOk());
    }

    @Test
    public void toSetupTourList() {
        // тестируем завершение второго этапа настройки турнира

        // игроки
        final Player p1 = (Player) accountService.create(AccountServiceTest.createPlayer("p1"));
        final Player p2 = (Player) accountService.create(AccountServiceTest.createPlayer("p2"));
        final Player p3 = (Player) accountService.create(AccountServiceTest.createPlayer("p3"));

        // лиги
        // лига без количества туров
        final League l1 = leagueService.create(new League("l1", null, null, null));
        // лига с 1 туром
        final League l2 = leagueService.create(new League("l2", null, null, 1));

        // чемпионат с лигой без количества туров - не прокатит
        final Tournament ch = tournamentService.create(new Championship("ch", l1.getId(), null, null), Arrays.asList(p1, p2));
        tournamentService.toSetupLeagueAndTourCountStatus(ch.getId());
        Assert.assertFalse(tournamentService.toSetupTourList(ch.getId()).isOk());

        // чемпионат с лигой, с указанным и подходящим количеством туров
        final Tournament ch1 = tournamentService.create(new Championship("ch1", l2.getId(), null, l2.getTourCount()), Arrays.asList(p1, p2));
        tournamentService.toSetupLeagueAndTourCountStatus(ch1.getId());
        // тут все хорошо
        final TournamentStatusModifyResult tsmr = tournamentService.toSetupTourList(ch1.getId());
        Assert.assertTrue(tsmr.isOk());
        Assert.assertNotNull(tsmr.getT());
        // и у турнира сменился статус
        Assert.assertEquals(tsmr.getT().getStatus(), TournamentStatus.CONFIGURATION_TOUR_LIST_SETTINGS);
        // проверим так же, что были создан 1 тур и 1 встреча
        final List<Tour> tours = tourService.findByTournamentIdAndDeletedFalse(ch1.getId());
        Assert.assertEquals(tours.size(), 1);
        Assert.assertEquals(meetingService.findByTourIdAndDeletedFalse(tours.get(0).getId()).size(), 1);

        // чемпионат без лиги и количество туров не указано
        final Tournament ch2 = tournamentService.create(new Championship("ch2", null, null, null), Arrays.asList(p1, p2));
        tournamentService.toSetupLeagueAndTourCountStatus(ch2.getId());
        Assert.assertFalse(tournamentService.toSetupTourList(ch2.getId()).isOk());

        // чемпионат без лиги и указано неверное количество туров
        final Tournament ch3 = tournamentService.create(new Championship("ch3", null, 10, 2), Arrays.asList(p1, p2, p3));
        tournamentService.toSetupLeagueAndTourCountStatus(ch3.getId());
        Assert.assertFalse(tournamentService.toSetupTourList(ch3.getId()).isOk());

        // чемпионат без лиги и указано верно количество туров
        final Tournament ch4 = tournamentService.create(new Championship("ch4", null, null, 1), Arrays.asList(p1, p2));
        tournamentService.toSetupLeagueAndTourCountStatus(ch4.getId());
        Assert.assertTrue(tournamentService.toSetupTourList(ch4.getId()).isOk());
        // а так же проверим, что было проставлено количество кругов
        final Championship ch4Updated = (Championship) tournamentService.getById(ch4.getId(), false);
        Assert.assertNotNull(ch4Updated.getRoundCount());
        Assert.assertEquals((int) ch4Updated.getRoundCount(), 1);
    }
}