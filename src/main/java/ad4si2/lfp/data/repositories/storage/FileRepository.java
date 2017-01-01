package ad4si2.lfp.data.repositories.storage;

import ad4si2.lfp.data.entities.storage.LfpFile;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends RepositoryWithDeleted<LfpFile, Long> {

}
