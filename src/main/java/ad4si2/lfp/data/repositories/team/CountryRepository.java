package ad4si2.lfp.data.repositories.team;

import ad4si2.lfp.data.entities.team.Country;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

@Repository
public interface CountryRepository extends RepositoryWithDeleted<Country, Long> {

    @Nonnull
    List<Country> findByNameAndDeletedFalse(@Nonnull final String name);
}
