package ad4si2.lfp.data.services.football;

import ad4si2.lfp.BaseTest;
import ad4si2.lfp.TestUtils;
import ad4si2.lfp.data.entities.football.Country;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class CountryServiceTest extends BaseTest {

    @Inject
    private CountryService countryService;

    @Nonnull
    public static Country createCountry(@Nonnull final String text) {
        return new Country(text, text);
    }

    @Test
    public void validationTest() {
        // ошибка при пустом названии страны
        TestUtils.validateCreateFailTest(countryService, new Country("", null));

        final String countryText = "country";
        final Country country = countryService.create(createCountry(countryText));

        // ошибка при создании страны с таким же названием, как уже есть
        TestUtils.validateDuplicateTest(countryService, country, createCountry(countryText));
    }

    @Test
    public void serviceTest() {

    }
}