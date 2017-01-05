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
@Table(name = "meeting")
public class Meeting implements Serializable, IDeleted, IEntity<Long, Meeting>, IAccountable {

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
    private long playerAId;

    @Column(nullable = false)
    private long playerBId;

    @Column(nullable = false)
    private long tourId;

    @Transient
    @Nullable
    private Account account;

    public Meeting() {
    }

    public Meeting(@Nonnull final Meeting other) {
        this.id = other.id;
        this.d = other.d;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.playerAId = other.playerAId;
        this.playerBId = other.playerBId;
        this.tourId = other.tourId;
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
    public Meeting copy() {
        return new Meeting(this);
    }

    @Override
    public String toString() {
        return "Meeting {" +
                "id=" + id +
                ", d=" + d +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", playerAId=" + playerAId +
                ", playerBId=" + playerBId +
                ", tourId=" + tourId +
                '}';
    }

    public long getPlayerAId() {
        return playerAId;
    }

    public long getPlayerBId() {
        return playerBId;
    }

    public long getTourId() {
        return tourId;
    }
}
