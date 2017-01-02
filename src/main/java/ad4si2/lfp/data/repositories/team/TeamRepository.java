package ad4si2.lfp.data.repositories.team;

import ad4si2.lfp.data.entities.team.Team;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface TeamRepository extends RepositoryWithDeleted<Team, Long> {

    @Nonnull
    List<Team> findByNameAndCityAndDeletedFalse(@Nonnull final String name, @Nonnull final String city);
}
