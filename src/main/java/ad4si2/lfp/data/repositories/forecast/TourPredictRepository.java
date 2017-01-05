package ad4si2.lfp.data.repositories.forecast;

import ad4si2.lfp.data.entities.forecast.TourPredict;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface TourPredictRepository extends RepositoryWithDeleted<TourPredict, Long> {

    @Nonnull
    List<TourPredict> findByTourIdAndDeletedFalse(final long tourId);
}
