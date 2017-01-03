package ad4si2.lfp.data.repositories.tournament;

import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface TournamentRepository extends RepositoryWithDeleted<Tournament, Long> {

    @Nonnull
    List<Tournament> findByNameAndDeletedFalse(@Nonnull final String name);

}
