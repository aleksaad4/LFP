package ad4si2.lfp.data.entities.football;

public enum MatchStatus {

    NOT_STARTED("Не стартовал"),
    PROGRESS("Идёт"),
    FINISH("Закончен");

    private String title;

    MatchStatus(final String title) {
        this.title = title;
    }

}
