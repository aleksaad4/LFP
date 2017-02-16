package ad4si2.lfp.data.services.football;

import ad4si2.lfp.BaseTest;
import ad4si2.lfp.TestUtils;
import ad4si2.lfp.data.entities.football.Country;
import ad4si2.lfp.data.entities.football.Team;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class TeamServiceTest extends BaseTest {

    @Inject
    private CountryService countryService;

    @Inject
    private TeamService teamService;

    @Nonnull
    public static Team createTeam(@Nonnull final String text, final long countryId) {
        return new Team(text, text, text, countryId);
    }

    @Test
    public void validationTest() {
        final Country c = countryService.create(CountryServiceTest.createCountry("country"));

        // ошибка при пустом названии команды
        TestUtils.validateCreateFailTest(teamService, new Team("", "city", null, c.getId()));
        // ошибка при пустом названии города
        TestUtils.validateCreateFailTest(teamService, new Team("team", "", null, c.getId()));

        final String teamText = "team";
        final Team team = teamService.create(createTeam(teamText, c.getId()));

        // ошибка при создании команды с таким же названием и городом, как уже есть
        TestUtils.validateDuplicateTest(teamService, team, createTeam(teamText, c.getId()));

        // ошибка при указании несуществующей страны в качестве страны команды
        TestUtils.validateCreateFailTest(teamService, createTeam("t1", 10L));
    }

    @Test
    public void eventListenerTest() {
        final Country country = countryService.create(CountryServiceTest.createCountry("country"));
        final Team team = teamService.create(createTeam("team", country.getId()));

        // нельзя удалить страну, если есть команда, привязанная к этой стране
        TestUtils.catchExceptionTest(() -> countryService.delete(country));

        // после удаления команды можно удалить страну
        teamService.delete(team);
        countryService.delete(country);
    }
}