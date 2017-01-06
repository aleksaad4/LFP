package ad4si2.lfp.web.controllers.admin.tounament;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.Player;
import ad4si2.lfp.data.entities.football.League;
import ad4si2.lfp.data.entities.tournament.TournamentStatus;
import ad4si2.lfp.data.entities.tournament.TournamentType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TournamentDTO {

    private long id;

    @Nonnull
    private String name;

    @Nonnull
    private TournamentType type;

    @Nullable
    private Integer roundCount;

    @Nullable
    private Integer tourCount;

    @Nullable
    private TournamentStatus status;

    @Nullable
    private Account account;

    @Nonnull
    private List<Player> players = new ArrayList<>();

    @Nullable
    private League league;

    @Nullable
    private Date creationDate;

    public TournamentDTO(final long id, @Nonnull final Date creationDate, @Nonnull final String name,
                         @Nonnull final TournamentType type,
                         @Nullable final TournamentStatus status, @Nullable final Account account,
                         @Nullable final League league) {
        this.id = id;
        this.creationDate = creationDate;
        this.name = name;
        this.type = type;
        this.status = status;
        this.account = account;
        this.league = league;
    }

    public void setRoundCount(@Nullable final Integer roundCount) {
        this.roundCount = roundCount;
    }

    public void setPlayers(@Nonnull final List<Player> players) {
        this.players = players;
    }

    public long getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public TournamentType getType() {
        return type;
    }

    @Nullable
    public Integer getRoundCount() {
        return roundCount;
    }

    @Nullable
    public TournamentStatus getStatus() {
        return status;
    }

    @Nullable
    public Account getAccount() {
        return account;
    }

    @Nonnull
    public List<Player> getPlayers() {
        return players;
    }

    @Nullable
    public League getLeague() {
        return league;
    }

    @Nullable
    public Date getCreationDate() {
        return creationDate;
    }

    public void setTourCount(@Nullable final Integer tourCount) {
        this.tourCount = tourCount;
    }

    @Nullable
    public Integer getTourCount() {
        return tourCount;
    }
}
