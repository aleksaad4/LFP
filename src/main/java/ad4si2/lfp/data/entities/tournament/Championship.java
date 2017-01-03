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
     * Только для турнира с типом
     */
    @Nullable
    @Column
    private Integer roundCount;

    public Championship() {
    }

    public Championship(final Championship other) {
        super(other);
        this.roundCount = other.roundCount;
    }

    public Championship(final long id, @Nonnull final Date creationDate, @Nonnull final String name, final TournamentType type, @Nullable final Integer roundCount) {
        super(id, creationDate, name, type);
        this.roundCount = roundCount;
    }

    @Nullable
    public Integer getRoundCount() {
        return roundCount;
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
                "} " + super.toString();
    }
}
