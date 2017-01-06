package ad4si2.lfp.data.services.football;

import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.repositories.football.MatchRepository;
import ad4si2.lfp.utils.data.IAccountCRUDService;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public interface MatchService extends IAccountCRUDService<Match, Long, MatchRepository> {

    @Nonnull
    List<Match> findByTourIdAndDeletedFalse(final long tourId);

    @Nonnull
    List<Match> findByTourIdInAndDeletedFalse(final Set<Long> tourIds);
}
