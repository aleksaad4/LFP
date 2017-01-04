package ad4si2.lfp.data.entities.tournament;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Чемпионат = Турнир с типом CUP
 */
@Entity
public class Cup extends Tournament {

    public Cup() {
    }

    public Cup(final Cup other) {
        super(other);
    }

    public Cup(final long id, @Nonnull final Date creationDate, @Nonnull final String name,
               @Nonnull final TournamentType type, @Nonnull final TournamentStatus status) {
        super(id, creationDate, name, type, status);
    }

    @Nonnull
    @Override
    public Cup copy() {
        return new Cup(this);
    }

    @Override
    public String toString() {
        return "Cup {} " + super.toString();
    }
}
