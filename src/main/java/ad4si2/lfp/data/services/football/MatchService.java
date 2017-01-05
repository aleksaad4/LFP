package ad4si2.lfp.data.services.football;

import ad4si2.lfp.data.entities.football.Match;
import ad4si2.lfp.data.repositories.football.MatchRepository;
import ad4si2.lfp.utils.data.IAccountCRUDService;

public interface MatchService extends IAccountCRUDService<Match, Long, MatchRepository> {

}
