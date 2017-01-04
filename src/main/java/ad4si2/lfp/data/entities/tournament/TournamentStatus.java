package ad4si2.lfp.data.entities.tournament;

public enum TournamentStatus {

    // на этапе выбора игроков можно добавлять и удалять игроков из турнира
    CONFIGURATION_PLAYERS_SETTINGS("Настройка: выбор игроков", true),
    CONFIGURATION_TOUR_COUNT_SETTINGS("Настройка: определение списка туров", true),
    CONFIGURATION_TOUR_LIST_SETTINGS("Настройка: создание туров", true),

    CREATED("Не стартовал", false),
    PROGRESS("Идёт", false),
    FINISH("Закончен", false);

    private String title;

    private boolean isConfiguration;

    TournamentStatus(final String title, final boolean isConfiguration) {
        this.title = title;
        this.isConfiguration = isConfiguration;
    }

    public boolean isConfiguration() {
        return isConfiguration;
    }
}
