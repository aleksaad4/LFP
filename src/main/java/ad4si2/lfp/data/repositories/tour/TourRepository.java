package ad4si2.lfp.data.repositories.tour;

import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface TourRepository extends RepositoryWithDeleted<Tour, Long> {

    @Nonnull
    List<Tour> findByTournamentIdAndDeletedFalse(final long tId);
}
