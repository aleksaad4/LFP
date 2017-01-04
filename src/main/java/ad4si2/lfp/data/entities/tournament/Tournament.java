package ad4si2.lfp.data.entities.tournament;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.utils.data.IAccountable;
import ad4si2.lfp.utils.data.IDeleted;
import ad4si2.lfp.utils.data.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Inheritance
@Entity
@Table(name = "tournament")
public abstract class Tournament implements Serializable, IDeleted, IEntity<Long, Tournament>, IAccountable {

    @Id
    @GeneratedValue
    private long id;

    @Nonnull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @Nonnull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date d = new Date();

    @Column(nullable = false)
    private boolean deleted = false;

    @Nullable
    @Column
    private Long accountId;

    @Nonnull
    @Column(nullable = false)
    private String name;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status = TournamentStatus.CONFIGURATION_PLAYERS_SETTINGS;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentType type;

    /**
     * Какая лига лежит в основе турнира
     * Может быть использовано, например, для автоматической загрузки и обновлении календаря футбольных матчей
     * А так же загрузки онлайн-результатов
     */
    @Nullable
    @Column
    private Long leagueId;

    @Transient
    @Nullable
    private Account account;

    public Tournament() {
    }

    public Tournament(@Nonnull final Tournament other) {
        this.id = other.id;
        this.d = other.d;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.name = other.name;
        this.status = other.status;
        this.type = other.type;
        this.leagueId = other.leagueId;
        this.creationDate = other.creationDate;
    }

    public Tournament(final long id, @Nonnull final Date creationDate, @Nonnull final String name,
                      @Nonnull final TournamentType type, @Nonnull final TournamentStatus status, @Nullable final Long leagueId) {
        this.creationDate = creationDate;
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.leagueId = leagueId;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    @Nullable
    @Override
    public Long getAccountId() {
        return accountId;
    }

    @Override
    public void setAccountId(@Nonnull final Long accountId) {
        this.accountId = accountId;
    }

    @Nullable
    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void setAccount(@Nullable final Account account) {
        this.account = account;
    }

    @Nonnull
    @Override
    public Long getId() {
        return id;
    }

    @Nonnull
    @Override
    public Date getD() {
        return d;
    }

    @Override
    public void setD(@Nonnull final Date d) {
        this.d = d;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(@Nonnull final TournamentStatus status) {
        this.status = status;
    }

    @Nonnull
    public TournamentType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Tournament {" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", d=" + d +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", leagueId=" + leagueId +
                '}';
    }

    @Nullable
    public Long getLeagueId() {
        return leagueId;
    }

    @Nonnull
    public Date getCreationDate() {
        return creationDate;
    }
}
