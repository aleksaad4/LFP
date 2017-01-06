package ad4si2.lfp.engine;

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
        // (для нечетного числа - последние строка и столбец выкидываются)
        for (int i = 0; i < playerCount; i++) {
            for (int j = 0; j < playerCount; j++) {
                result[i][j] = seedTable[i][j];
            }
        }

        return result;
    }

    /**
     * Рекурсивный метод заполняет таблицу встреч
     *
     * @param table     таблица встреч
     * @param pos       текущая позиция подтаблицы
     * @param size      текущий размер подтаблицы
     * @param startTour текущий тур
     * @return          новый текущий тур
     */
    private int fillSeedTable(final int[][] table, final int pos, final int size, int startTour) {
        if (size == 2) {
            // базовая таблица
            // Х
            // 0 Х
            table[pos + 1][pos] = startTour;
            return startTour + 1;
        } else if ((size / 2) % 2 == 0) {
            // четная таблица - размер половины таблицы четный
            // заполняем две диагональные четверти, затем квадратную четверть
            final int halfSize = size / 2;

            // верхняя и нижняя диагональные четверти
            fillSeedTable(table, pos, halfSize, startTour);
            startTour = fillSeedTable(table, pos + halfSize, halfSize, startTour);

            // квадратная четверть
            for (int i = 0; i < halfSize; i++) {
                for (int j = 0; j < halfSize; j++) {
                    final int index = (i + j + 1) % halfSize;
                    table[pos + halfSize + i][pos + j] = startTour + index;
                }
            }

            return startTour + halfSize;
        } else { // ((size / 2) % 2 == 1)
            // нечетная таблица - размер половины таблицы нечетный
            // заполняем две диагональные четверти и одновременно диагональ квадратной,
            // затем остаток квадратной четверти
            final int halfSize = size / 2;

            // верхняя и нижняя диагональные четверти
            for (int i = 0; i < halfSize; i++) {
                for (int j = 0; j <= i; j++) {
                    final int index = (i + j + 1) % halfSize;
                    table[pos + i][pos + j] = startTour + index;
                    table[pos + size - j - 1][pos + size - i - 1] = startTour + index;
                }
            }

            // диагональ квадратной четверти
            for (int i = 0; i < halfSize; i++) {
                table[pos + size - i - 1][pos + i] = table[pos + i][pos + i];
            }

            startTour += halfSize;

            // квадратная четверть без диагонали
            for (int i = 0; i < halfSize; i++) {
                for (int j = 0; j < halfSize; j++) {
                    if (i == halfSize - j - 1) {
                        continue;
                    }

                    final int index = (i + j) % halfSize;
                    table[pos + halfSize + i][pos + j] = startTour + index;
                }
            }

            // диагональ тура не дала, так что на 1 меньше
            return startTour + halfSize - 1;
        }
    }
}
