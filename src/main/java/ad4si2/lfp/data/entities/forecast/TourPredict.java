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
@Table(name = "tour_predict")
public class TourPredict implements Serializable, IDeleted, IEntity<Long, TourPredict>, IAccountable {

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

    @Nullable
    @Column
    private Integer score;

    @Column(nullable = false)
    private long tourId;

    @Column(nullable = false)
    private long playerId;

    @Transient
    @Nullable
    private Account account;

    public TourPredict() {
    }

    public TourPredict(@Nonnull final TourPredict other) {
        this.id = other.id;
        this.d = other.d;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.score = other.score;
        this.tourId = other.tourId;
        this.playerId = other.playerId;
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
    public TourPredict copy() {
        return new TourPredict(this);
    }

    @Nullable
    public Integer getScore() {
        return score;
    }

    public long getTourId() {
        return tourId;
    }

    @Override
    public String toString() {
        return "TourPredict {" +
                "id=" + id +
                ", d=" + d +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", score=" + score +
                ", tourId=" + tourId +
                ", playerId=" + playerId +
                '}';
    }

    public long getPlayerId() {
        return playerId;
    }
}
