package ad4si2.lfp.data.entities.tournament;

public enum TournamentType {

    CHAMPIONSHIP("Чемпионат", 2),
    CUP("Кубок", 1),
    CHAMPIONSHIP_WITH_PLAY_OFF("Чемпионат с плейофом", 2);

    private String title;
    private int minPlayersCount;

    TournamentType(final String title, final int minPlayersCount) {
        this.title = title;
        this.minPlayersCount = minPlayersCount;
    }

    public int getMinPlayersCount() {
        return minPlayersCount;
    }
}
