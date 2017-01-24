package ad4si2.lfp.data.entities.tour;

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
@Table(name = "tour")
public class Tour implements Serializable, IDeleted, IEntity<Long, Tour>, IAccountable {

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

    @Nonnull
    @Column(nullable = false)
    private String name;

    @Column
    private long tournamentId;

    @Nullable
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date openDate;

    @Nullable
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Nullable
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status = TourStatus.NOT_STARTED;

    @Transient
    @Nullable
    private Account account;

    public Tour(@Nonnull final String name, final long tournamentId) {
        this.name = name;
        this.tournamentId = tournamentId;
    }

    public Tour() {
    }

    public Tour(final long id, @Nonnull final String name, final long tournamentId,
                @Nullable final Date openDate, @Nullable final Date startDate, @Nullable final Date finishDate, @Nullable final TourStatus status) {
        this.id = id;
        this.name = name;
        this.tournamentId = tournamentId;
        this.openDate = openDate;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.status = status;
    }

    public Tour(@Nonnull final Tour other) {
        this.id = other.id;
        this.d = other.d;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.name = other.name;
        this.tournamentId = other.tournamentId;
        this.openDate = other.openDate;
        this.startDate = other.startDate;
        this.finishDate = other.finishDate;
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
    @Override
    public Date getD() {
        return d;
    }

    @Override
    public void setD(@Nonnull final Date d) {
        this.d = d;
    }

    @Nonnull
    @Override
    public Tour copy() {
        return new Tour(this);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Tour {" +
                "id=" + id +
                ", d=" + d +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", name='" + name + '\'' +
                ", tournamentId=" + tournamentId +
                ", openDate=" + openDate +
                ", startDate=" + startDate +
                ", finishDate=" + finishDate +
                ", status=" + status +
                '}';
    }

    @Nullable
    public Date getOpenDate() {
        return openDate;
    }

    @Nullable
    public Date getStartDate() {
        return startDate;
    }

    @Nullable
    public Date getFinishDate() {
        return finishDate;
    }

    @Nonnull
    public TourStatus getStatus() {
        return status;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    public void setOpenDate(@Nullable final Date openDate) {
        this.openDate = openDate;
    }

    public void setStartDate(@Nullable final Date startDate) {
        this.startDate = startDate;
    }

    public void setFinishDate(@Nullable final Date finishDate) {
        this.finishDate = finishDate;
    }
}
