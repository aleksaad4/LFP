package ad4si2.lfp.data.repositories.football;

import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.entities.football.MatchStatus;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Repository
public interface MatchRepository extends RepositoryWithDeleted<Match, Long> {

    @Nonnull
    List<Match> findByTourIdAndDeletedFalse(final long tourId);

    @Nonnull
    List<Match> findByTeamAIdOrTeamBId(final long ta, final long tb);
}
