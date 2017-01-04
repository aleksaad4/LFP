package ad4si2.lfp.data.services.tournament;

import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.data.entities.tournament.TournamentPlayerLink;
import ad4si2.lfp.data.repositories.tournament.TournamentRepository;
import ad4si2.lfp.utils.data.IAccountCRUDService;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public interface TournamentService extends IAccountCRUDService<Tournament, Long, TournamentRepository> {

    @Nonnull
    Tournament create(@Nonnull final Tournament tournament, @Nonnull final List<Player> players);

    @Nonnull
    Tournament update(@Nonnull final Tournament tournament, @Nonnull final List<Player> players);

    @Nonnull
    List<TournamentPlayerLink> findTournamentPlayerLinks(final long tournamentId);

    @Nonnull
    List<TournamentPlayerLink> findTournamentPlayerLinks(@Nonnull final Set<Long> tournamentIds);

    @Nonnull
    TournamentStatusModifyResult finishFirstStep(final long tournamentId);
}
