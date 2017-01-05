package ad4si2.lfp.data.entities.forecast;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.utils.data.IAccountable;
import ad4si2.lfp.utils.data.IDeleted;
import ad4si2.lfp.utils.data.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "match_predict")
public class MatchPredict implements Serializable, IDeleted, IEntity<Long, MatchPredict>, IAccountable {

    @Id
    @GeneratedValue
    private long id;

    @Nonnull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date d = new Date();

    @Column(nullable = false)
    private boolean deleted = false;

    @Nullable
    @Column
    private Long accountId;

    @Column(nullable = false)
    private int aGoals;

    @Column(nullable = false)
    private int bGoals;

    @Column(nullable = false)
    private long tourPredictId;

    @Column(nullable = false)
    private long matchId;

    @Transient
    @Nullable
    private Account account;

    public MatchPredict() {
    }

    public MatchPredict(@Nonnull final MatchPredict other) {
        this.id = other.id;
        this.d = other.d;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.aGoals = other.aGoals;
        this.bGoals = other.bGoals;
        this.tourPredictId = other.tourPredictId;
        this.matchId = other.matchId;
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
        return d;
    }

    public void setD(@Nonnull final Date d) {
        this.d = d;
    }

    @Nonnull
    @Override
    public MatchPredict copy() {
        return new MatchPredict(this);
    }

    @Override
    public String toString() {
        return "MatchPredict {" +
                "id=" + id +
                ", d=" + d +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", aGoals=" + aGoals +
                ", bGoals=" + bGoals +
                ", tourPredictId=" + tourPredictId +
                ", matchId=" + matchId +
                '}';
    }

    public int getaGoals() {
        return aGoals;
    }

    public int getbGoals() {
        return bGoals;
    }

    public long getTourPredictId() {
        return tourPredictId;
    }

    public long getMatchId() {
        return matchId;
    }
}
