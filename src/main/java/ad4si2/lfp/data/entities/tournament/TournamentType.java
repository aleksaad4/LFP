package ad4si2.lfp.data.entities.tournament;

public enum TournamentType {

    CHAMPIONSHIP("Чемпионат"),
    CUP("Кубок"),
    CHAMPIONSHIP_WITH_PLAY_OFF("Чемпионат с плейофом");

    public String title;

    TournamentType(final String title) {
        this.title = title;
    }
}
