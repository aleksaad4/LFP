package ad4si2.lfp.data.entities.football;

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
@Table(name = "match_result")
public class MatchResult implements Serializable, IDeleted, IEntity<Long, MatchResult>, IAccountable {

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
    private long matchId;

    @Column(nullable = false)
    private int aGoals;

    @Column(nullable = false)
    private int bGoals;

    @Transient
    @Nullable
    private Account account;

    public MatchResult() {
    }

    public MatchResult(@Nonnull final MatchResult other) {
        this.id = other.id;
        this.d = other.d;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.matchId = other.matchId;
        this.aGoals = other.aGoals;
        this.bGoals = other.bGoals;
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
    public MatchResult copy() {
        return new MatchResult(this);
    }

    @Override
    public String toString() {
        return "MatchResult {" +
                "id=" + id +
                ", d=" + d +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", matchId=" + matchId +
                ", aGoals=" + aGoals +
                ", bGoals=" + bGoals +
                '}';
    }

    public long getMatchId() {
        return matchId;
    }

    public int getaGoals() {
        return aGoals;
    }

    public int getbGoals() {
        return bGoals;
    }
}
