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
@Table(name = "country")
public class Country implements Serializable, IDeleted, IEntity<Long, Country>, IAccountable {

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

    @Nullable
    @Column
    private String imageUrl;

    @Transient
    @Nullable
    private Account account;

    protected Country() {
    }

    public Country(@Nonnull final String name, @Nullable final String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Country(@Nonnull final Country other) {
        this.id = other.id;
        this.d = other.d;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.name = other.name;
        this.imageUrl = other.imageUrl;
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
    public Country copy() {
        return new Country(this);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Country {" +
                "id=" + id +
                ", d=" + d +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", name='" + name + '\'' +
                ", iconUrl='" + imageUrl + '\'' +
                ", account=" + account +
                '}';
    }
}
