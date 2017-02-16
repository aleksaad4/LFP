package ad4si2.lfp.data.entities.tournament;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Чемпионат = Турнир с типом CUP
 */
@Entity
public class Cup extends Tournament {

    protected Cup() {
    }

    public Cup(final Cup other) {
        super(other);
    }

    public Cup(final long id, @Nonnull final Date creationDate, @Nonnull final String name,
               @Nonnull final TournamentStatus status, @Nullable final Long leagueId) {
        super(id, creationDate, name, TournamentType.CUP, status, leagueId);
    }

    public Cup(@Nonnull final String name, @Nullable final Long leagueId) {
        super(name, TournamentType.CUP, leagueId);
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
