package ad4si2.lfp.data.services.football;

import ad4si2.lfp.BaseTest;
import ad4si2.lfp.TestUtils;
import ad4si2.lfp.data.entities.football.Country;
import ad4si2.lfp.data.entities.football.League;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class LeagueServiceTest extends BaseTest {

    @Inject
    private LeagueService leagueService;

    @Inject
    private CountryService countryService;

    @Nonnull
    public static League createLeague(@Nonnull final String text) {
        return new League(text, text);
    }

    @Nonnull
    public static League createLeague(@Nonnull final String text, final long countryId) {
        return new League(text, text, countryId, null);
    }

    @Test
    public void validationTest() {
        // ошибка при пустом названии лиги
        TestUtils.validateCreateFailTest(leagueService, new League("", null));

        final String leagueText = "league";
        final League league = leagueService.create(createLeague(leagueText));

        // ошибка при создании лиги с таким же названием, как уже есть
        TestUtils.validateDuplicateTest(leagueService, league, createLeague(leagueText));

        // ошибка при указании несуществующей страны в качестве страны лиги
        TestUtils.validateCreateFailTest(leagueService, createLeague("l1", 1));

        // ошибка при указании нулевого или отрицательного количества туров
        TestUtils.validateCreateFailTest(leagueService, new League("l1", null, null, 0));
        TestUtils.validateCreateFailTest(leagueService, new League("l1", null, null, -1));
    }

    @Test
    public void eventListenerTest() {
        final Country country = countryService.create(CountryServiceTest.createCountry("country"));
        final League league = leagueService.create(createLeague("league", country.getId()));

        // нельзя удалить страну, если есть лига, привязанная к этой стране
        TestUtils.catchExceptionTest(() -> countryService.delete(country));

        // после удаления лиги можно удалить страну
        leagueService.delete(league);
        countryService.delete(country);
    }
}