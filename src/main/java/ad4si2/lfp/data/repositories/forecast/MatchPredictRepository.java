package ad4si2.lfp.data.repositories.forecast;

import ad4si2.lfp.data.entities.forecast.MatchPredict;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface MatchPredictRepository extends RepositoryWithDeleted<MatchPredict, Long> {

    @Nonnull
    List<MatchPredict> findByMatchIdAndDeletedFalse(final long matchId);
}
