package ad4si2.lfp.data.entities.tournament;

import javax.persistence.*;

@Entity
@Table(name = "tournament_player")
public class TournamentPlayerLink {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private long playerId;

    @Column(nullable = false)
    private long tournamentId;

    public TournamentPlayerLink() {
    }

    public TournamentPlayerLink(final long playerId, final long tournamentId) {
        this.playerId = playerId;
        this.tournamentId = tournamentId;
    }

    public long getId() {
        return id;
    }

    public long getPlayerId() {
        return playerId;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    @Override
    public String toString() {
        return "TournamentPlayerLink {" +
                "id=" + id +
                ", playerId=" + playerId +
                ", tournamentId=" + tournamentId +
                '}';
    }
}
