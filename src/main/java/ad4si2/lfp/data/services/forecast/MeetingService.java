package ad4si2.lfp.data.services.forecast;

import ad4si2.lfp.data.entities.forecast.Meeting;
import ad4si2.lfp.data.repositories.forecast.MeetingRepository;
import ad4si2.lfp.utils.data.IAccountCRUDService;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public interface MeetingService extends IAccountCRUDService<Meeting, Long, MeetingRepository> {

    @Nonnull
    List<Meeting> findByTourIdAndDeletedFalse(final long tourId);

    @Nonnull
    List<Meeting> findByTourIdInAndDeletedFalse(final Set<Long> tourIds);

}
