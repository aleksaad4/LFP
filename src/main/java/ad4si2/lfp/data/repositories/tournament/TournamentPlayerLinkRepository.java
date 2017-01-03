package ad4si2.lfp.data.repositories.tournament;

import ad4si2.lfp.data.entities.tournament.TournamentPlayerLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

@Repository
public interface TournamentPlayerLinkRepository extends JpaRepository<TournamentPlayerLink, Long> {

    @Nonnull
    List<TournamentPlayerLink> findByTournamentId(final long tournamentId);

    @Nonnull
    List<TournamentPlayerLink> findByTournamentIdIn(final Collection<Long> tournamentIds);
}
