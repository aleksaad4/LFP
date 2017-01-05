package ad4si2.lfp.data.entities.tour;

public enum TourStatus {

    NOT_STARTED("Не стартовал"),
    // можно делать прогнозы
    OPEN("Открыт"),
    // начались футбольные матчи
    PROGRESS("Идёт"),
    FINISH("Закончен");

    private String title;

    TourStatus(final String title) {
        this.title = title;
    }
}
