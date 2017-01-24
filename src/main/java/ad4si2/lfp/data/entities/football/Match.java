package ad4si2.lfp.data.entities.football;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.utils.data.IAccountable;
import ad4si2.lfp.utils.data.IDeleted;
import ad4si2.lfp.utils.data.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "f_match")
public class Match implements Serializable, IDeleted, IEntity<Long, Match>, IAccountable {

    @Id
    @GeneratedValue
    private long id;

    @Nonnull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastEditDate = new Date();

    @Column(nullable = false)
    private boolean deleted = false;

    @Nullable
    @Column
    private Long accountId;

    @Nullable
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(nullable = false)
    private long teamAId;

    @Column(nullable = false)
    private long teamBId;

    @Column(nullable = false)
    private boolean teamAIsHome = true;

    @Column(nullable = false)
    private long tourId;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.NOT_STARTED;

    @Transient
    @Nullable
    private Account account;

    public Match() {
    }

    public Match(@Nonnull final Match other) {
        this.id = other.id;
        this.lastEditDate = other.lastEditDate;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.date = other.date;
        this.teamAId = other.teamAId;
        this.teamBId = other.teamBId;
        this.teamAIsHome = other.teamAIsHome;
        this.tourId = other.tourId;
        this.status = other.status;
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
    public Date getD() {
        return lastEditDate;
    }

    public void setD(@Nonnull final Date d) {
        this.lastEditDate = d;
    }

    @Nonnull
    @Override
    public Match copy() {
        return new Match(this);
    }

    @Nullable
    public Date getDate() {
        return date;
    }

    public long getTeamAId() {
        return teamAId;
    }

    public long getTeamBId() {
        return teamBId;
    }

    public long getTourId() {
        return tourId;
    }

    @Nonnull
    public MatchStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Match {" +
                "id=" + id +
                ", lastEditDate=" + lastEditDate +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", date=" + date +
                ", teamAId=" + teamAId +
                ", teamBId=" + teamBId +
                ", teamAIsHome=" + teamAIsHome +
                ", tourId=" + tourId +
                ", status=" + status +
                '}';
    }

    public boolean isTeamAIsHome() {
        return teamAIsHome;
    }

    public void setTourId(final long tourId) {
        this.tourId = tourId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Match match = (Match) o;

        return id == match.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
