package ad4si2.lfp.engine;

import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tournament.Championship;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DrawEngine {

    @Inject
    private ChampionshipEngine championshipEngine;

    /**
     * Метод для генерации списка встреч между игроками в турнире
     *
     * @param t         турнир
     * @param playerIds список id игроков
     * @param tours     туры
     * @return список встреч во всех турах
     */
    public List<Meeting> drawPlayers(@Nonnull final Championship t, @Nonnull final List<Long> playerIds, @Nonnull final List<Tour> tours) {
        final List<Meeting> result = new ArrayList<>();

        // располагаем игроков в случайном порядке
        Random random = new Random();
        int[] playerIndex = new int[playerIds.size()];
        for (int i = 0; i < playerIndex.length; i++) {
            playerIndex[i] = i;
        }
        for (int i = 0; i < playerIndex.length * playerIndex.length; i++) {
            int i1 = random.nextInt(playerIndex.length);
            int i2 = random.nextInt(playerIndex.length);
            int tmp = playerIndex[i1];
            playerIndex[i1] = playerIndex[i2];
            playerIndex[i2] = tmp;
        }

        // получаем таблицу встреч в круге для заданного числа игроков
        int[][] drawTable = getDrawTable(playerIds.size());

        int startTour = 0;
        int tourInRoundCount = championshipEngine.getTourInRoundCount(playerIds.size());

        for (int round = 0; round < t.getRoundCount(); round++) {
            // в каждом круге проходимся по левой нижней половине таблицы (i/строка > j/столбец)
            // и получаем из нее номера туров для встреч текущих игроков
            for (int i = 1; i < drawTable.length; i++) {
                for (int j = 0; j < i; j++) {
                    int indexI = playerIndex[i];
                    int indexJ = playerIndex[j];

                    // периодически меняем хозяина-гостя в паре
                    if ((indexI + indexJ + round) % 2 == 0) {
                        int tmp = indexI;
                        indexI = indexJ;
                        indexJ = tmp;
                    }

                    final Long playerIId = playerIds.get(indexI);
                    final Long playerJId = playerIds.get(indexJ);

                    Tour tour = tours.get(startTour + drawTable[i][j]);

                    Meeting meeting = new Meeting(playerIId, playerJId, tour.getId());
                    result.add(meeting);
                }
            }

            // переходим в новый круг
            startTour += tourInRoundCount;
        }

        return result;
    }

    /**
     * Метод создаем таблицу встреч для заданного числа игроков
     *
     * @param playerCount число игроков
     * @return таблица встреч
     */
    public int[][] getDrawTable(final int playerCount) {
        int[][] result = new int[playerCount][playerCount];

        // получаем размер таблицы (для нечетного числа надо увеличить на 1)
        int seedTableSize = ((playerCount + 1) / 2) * 2;

        // создаем посевочную таблицу
        int[][] seedTable = new int[seedTableSize][seedTableSize];
        fillSeedTable(seedTable, 0, seedTableSize, 0);

        // переносим данные в результат
        for (int i = 1; i < playerCount; i++) {
            for (int j = 0; j < i; j++) {
                result[i][j] = seedTable[i][j];
            }
        }

        return result;
    }

    private int fillSeedTable(final int[][] seedTable, final int pos, final int size, final int startTour) {
        if (size == 2) {
            // базовая таблица
            // Х
            // 0 Х
            seedTable[pos + 1][pos] = startTour;
            return startTour + 1;
        } else if ((size % 2) % 2 == 0) {
            // четная таблица - размер половины таблицы четный
            // заполняем две диагональные четверти, затем квадратную четверть
            // D1
            // S12 D2
            final int halfSize = size / 2;
            fillSeedTable(seedTable, pos, halfSize, startTour);
            int mergeStartTour = fillSeedTable(seedTable, pos + halfSize, halfSize, startTour);

            // слияние
            for (int i = 0; i < halfSize; i++) {
                for (int j = 0; j < halfSize; j++) {
                    final int mergeIndex = (i + j + 1) % halfSize;
                    seedTable[pos + halfSize + i][pos + j] = mergeStartTour + mergeIndex;
                }
            }

            return mergeStartTour + halfSize;
        } else { // ((size % 2) % 2 == 1)
            // нечетная таблица - размер половины таблицы нечетный
            // заполняем две диагональные четверти и одновременно диагональ квадратной,
            // затем остаток квадратной четверти
            final int halfSize = size / 2;

            return startTour + size;
        }
    }
}
