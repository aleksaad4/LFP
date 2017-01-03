package ad4si2.lfp.data.repositories.account;

import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.utils.data.RepositoryWithDeleted;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends RepositoryWithDeleted<Player, Long> {

}
