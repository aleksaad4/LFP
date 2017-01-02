package ad4si2.lfp.data.repositories.team;

import ad4si2.lfp.data.entities.team.League;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface LeagueRepository extends RepositoryWithDeleted<League, Long> {

    @Nonnull
    List<League> findByNameAndDeletedFalse(@Nonnull final String name);
}
