package ad4si2.lfp.data.entities.tournament;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Чемпионат = Турнир с типом CHAMPIONSHIP
 */
@Entity
public class Championship extends Tournament {

    /**
     * Количество кругов в чемпионате
     */
    @Nullable
    @Column
    private Integer roundCount;

    /**
     * Колличество туров в чемпионате
     */
    @Nullable
    @Column
    private Integer tourCount;

    protected Championship() {
    }

    public Championship(final Championship other) {
        super(other);
        this.roundCount = other.roundCount;
        this.tourCount = other.tourCount;
    }

    public Championship(final long id, @Nonnull final Date creationDate, @Nonnull final String name,
                        @Nonnull final TournamentStatus status, @Nullable final Long leagueId,
                        @Nullable final Integer roundCount, @Nullable final Integer tourCount) {
        super(id, creationDate, name, TournamentType.CHAMPIONSHIP, status, leagueId);
        this.roundCount = roundCount;
        this.tourCount = tourCount;
    }

    public Championship(@Nonnull final String name,
                        @Nullable final Long leagueId, @Nullable final Integer roundCount, @Nullable final Integer tourCount) {
        super(name, TournamentType.CHAMPIONSHIP, leagueId);
        this.roundCount = roundCount;
        this.tourCount = tourCount;
    }

    @Nullable
    public Integer getRoundCount() {
        return roundCount;
    }

    public void setRoundCount(@Nullable final Integer roundCount) {
        this.roundCount = roundCount;
    }

    @Nullable
    public Integer getTourCount() {
        return tourCount;
    }

    public void setTourCount(@Nullable final Integer tourCount) {
        this.tourCount = tourCount;
    }

    @Nonnull
    @Override
    public Championship copy() {
        return new Championship(this);
    }

    @Override
    public String toString() {
        return "Championship {" +
                "roundCount=" + roundCount +
                ", tourCount=" + tourCount +
                "} " + super.toString();
    }
}
