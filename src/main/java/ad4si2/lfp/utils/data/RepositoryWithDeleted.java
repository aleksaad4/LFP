package ad4si2.lfp.utils.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@NoRepositoryBean
@Transactional(propagation = Propagation.MANDATORY)
public interface RepositoryWithDeleted<T extends IDeleted, ID extends Serializable> extends JpaRepository<T, ID> {

    @Nonnull
    List<T> findAllByDeletedFalse();

    @Nonnull
    List<T> findAllByDeletedFalseAndIdIn(@Nonnull final Collection<ID> ids);

    @Nonnull
    List<T> findAllByIdIn(@Nonnull final Collection<ID> ids);

    @Nonnull
    List<T> findAllByDeletedFalseAndIdNotIn(@Nonnull final Collection<ID> ids);

    @Nonnull
    List<T> findAllByIdNotIn(@Nonnull final Collection<ID> ids);

    @Nullable
    T findByIdAndDeletedFalse(ID id);
}
