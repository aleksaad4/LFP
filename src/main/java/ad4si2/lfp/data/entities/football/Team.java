package ad4si2.lfp.data.entities.football;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.utils.data.IAccountable;
import ad4si2.lfp.utils.data.IDeleted;
import ad4si2.lfp.utils.data.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "team")
public class Team implements Serializable, IDeleted, IEntity<Long, Team>, IAccountable {

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

    @Nonnull
    @Column(nullable = false)
    private String city;

    @Nullable
    @Column
    private String imageUrl;

    @Column(nullable = false)
    private long countryId;

    @Transient
    @Nullable
    private Account account;

    @Transient
    @Nullable
    private Country country;

    protected Team() {
    }

    public Team(@Nonnull final String name, @Nonnull final String city, @Nullable final String imageUrl, final long countryId) {
        this.name = name;
        this.city = city;
        this.imageUrl = imageUrl;
        this.countryId = countryId;
    }

    public Team(@Nonnull final Team other) {
        this.id = other.id;
        this.d = other.d;
        this.deleted = other.deleted;
        this.accountId = other.accountId;
        this.name = other.name;
        this.imageUrl = other.imageUrl;
        this.countryId = other.countryId;
        this.city = other.city;
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
    public Team copy() {
        return new Team(this);
    }

    public void setCountry(@Nullable final Country country) {
        this.country = country;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public long getCountryId() {
        return countryId;
    }

    @Nullable
    public Country getCountry() {
        return country;
    }

    @Nonnull
    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "Team {" +
                "id=" + id +
                ", d=" + d +
                ", deleted=" + deleted +
                ", accountId=" + accountId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", iconUrl='" + imageUrl + '\'' +
                ", countryId=" + countryId +
                '}';
    }
}
