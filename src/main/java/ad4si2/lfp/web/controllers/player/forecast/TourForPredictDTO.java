package ad4si2.lfp.web.controllers.player.forecast;

import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tournament.Tournament;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TourForPredictDTO {

    @Nonnull
    private Tournament t;

    @Nonnull
    private Tour tour;

    @Nullable
    private Meeting meeting;

    private boolean isPredict;

    public TourForPredictDTO(@Nonnull final Tournament t, @Nonnull final Tour tour, @Nullable final Meeting meeting, final boolean isPredict) {
        this.t = t;
        this.tour = tour;
        this.meeting = meeting;
        this.isPredict = isPredict;
    }

    @Override
    public String toString() {
        return "TourForPredictDTO {" +
                "t=" + t +
                ", tour=" + tour +
                ", meeting=" + meeting +
                ", isPredict=" + isPredict +
                '}';
    }
}
