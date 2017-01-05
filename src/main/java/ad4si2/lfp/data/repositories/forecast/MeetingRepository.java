package ad4si2.lfp.data.repositories.forecast;

import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface MeetingRepository extends RepositoryWithDeleted<Meeting, Long> {

    @Nonnull
    List<Meeting> findByTourIdAndDeletedFalse(final long tourId);
}
