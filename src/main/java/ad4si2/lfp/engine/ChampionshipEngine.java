package ad4si2.lfp.engine;

import ad4si2.lfp.data.entities.football.League;
import ad4si2.lfp.data.entities.tournament.Championship;
import ad4si2.lfp.data.services.football.LeagueService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChampionshipEngine {

    // максимальное количество туров
    public static final int MAX_TOUR_COUNT = 100;

    @Inject
    private LeagueService leagueService;

    /**
     * Отметка подходящих лиг турнира (типа чемпионат)
     *
     * @param t           турнир типа чемпионат
     * @param playerCount количество игроков
     * @param leagues     все доступные для выбора лиги
     */
    @Nonnull
    public void markEnabledLeagues(@Nonnull final Championship t, final int playerCount, @Nonnull final List<League> leagues) {
        final Integer roundCount = t.getRoundCount();

        // количество туров в круге
        final int tourInRoundCount = getTourInRoundCount(playerCount);

        for (final League l : leagues) {
            // берем только лиги, в которых задано количество туров
            if (l.getTourCount() == null) {
                continue;
            }

            if (roundCount != null) {
                if (roundCount * tourInRoundCount == l.getTourCount()) {
                    l.setEnabled(true);
                }
            } else {
                if (l.getTourCount() % tourInRoundCount == 0) {
                    l.setEnabled(true);
                }
            }
        }
    }

    /**
     * Получение списка возможных количеств туров и кругов для турнира (типа чемпионат)
     *
     * @param t           турнир типа чемпионат
     * @param playerCount количество игроков
     * @return количества туров и кругов
     */
    @Nonnull
    public List<Pair<Integer, Integer>> getTourAndRoundCounts(@Nonnull final Championship t, final int playerCount) {
        final List<Pair<Integer, Integer>> result = new ArrayList<>();
        final Integer roundCount = t.getRoundCount();

        // количество туров в круге
        final int tourInRoundCount = getTourInRoundCount(playerCount);

        if (roundCount != null) {
            result.add(new ImmutablePair<>(tourInRoundCount * roundCount, roundCount));
        } else {
            for (int i = 1; i < MAX_TOUR_COUNT / tourInRoundCount; i++) {
                result.add(new ImmutablePair<>(tourInRoundCount * i, i));
            }
        }

        return result;
    }

    /**
     * Получение количества кругов по заданному количеству туров и количеству игроков
     *
     * @param tourCount   количество туров
     * @param playerCount количество игроков
     * @return количество кругов
     */
    public int getRoundCount(final int tourCount, final int playerCount) {
        return tourCount / getTourInRoundCount(playerCount);
    }

    /**
     * Получение количества туров в круге в зависимости от количества игроков
     *
     * @param playerCount количество игроков
     * @return количество туров в круге
     */
    public int getTourInRoundCount(final int playerCount) {
        return ((playerCount + 1) / 2) * 2 - 1;
    }
}
