package ad4si2.lfp.data.services.team;

import ad4si2.lfp.data.entities.team.Team;
import ad4si2.lfp.data.repositories.team.TeamRepository;
import ad4si2.lfp.utils.data.IAccountCRUDService;

public interface TeamService extends IAccountCRUDService<Team, Long, TeamRepository> {

}
