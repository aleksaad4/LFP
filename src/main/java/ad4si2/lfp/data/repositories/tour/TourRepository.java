package ad4si2.lfp.data.repositories.tour;

import ad4si2.lfp.data.entities.tour.Tour;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface TourRepository extends RepositoryWithDeleted<Tour, Long> {

    @Nonnull
    List<Tour> findByTournamentIdAndDeletedFalse(final long tId);

    @Nonnull
    List<Tour> findByOpenDateBeforeAndDeletedFalseAndStatus(@Nonnull final Date d,
                                                             @Nonnull final TourStatus status);

    @Nonnull
    List<Tour> findByStartDateBeforeAndDeletedFalseAndStatus(@Nonnull final Date d,
                                                              @Nonnull final TourStatus status);

    @Nonnull
    List<Tour> findByFinishDateBeforeAndDeletedFalseAndStatus(@Nonnull final Date d,
                                                               @Nonnull final TourStatus status);

    @Nonnull
    List<Tour> findByTournamentIdInAndStatusAndDeletedFalse(@Nonnull Collection<Long> tournamentIds, @Nonnull TourStatus status);
}
