package ad4si2.lfp.data.repositories.football;

import ad4si2.lfp.data.entities.football.MatchResult;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface MatchResultRepository extends RepositoryWithDeleted<MatchResult, Long> {

    @Nonnull
    List<MatchResult> findByMatchIdAndDeletedFalse(final long matchId);
}
