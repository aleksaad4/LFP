package ad4si2.lfp.data.services.football;

import ad4si2.lfp.data.entities.football.Team;
import ad4si2.lfp.data.repositories.football.TeamRepository;
import ad4si2.lfp.utils.data.IAccountCRUDService;

public interface TeamService extends IAccountCRUDService<Team, Long, TeamRepository> {

}
