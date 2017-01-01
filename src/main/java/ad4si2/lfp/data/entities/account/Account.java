package ad4si2.lfp.data.entities.account;

import ad4si2.lfp.utils.data.IAccountable;
import ad4si2.lfp.utils.data.IDeleted;
import ad4si2.lfp.utils.data.IEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "accounts")
public class Account implements Serializable, IDeleted, IEntity<Long, Account>, IAccountable {

    @Id
    @GeneratedValue
    private long id;

    @Nonnull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date d = new Date();

    @Nonnull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @Column(nullable = false)
    private boolean deleted = false;

    @Nonnull
    @Column(nullable = false)
    private String login;

    @Nonnull
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRole role;

    @Nullable
    @Column
    private String name;

    @Nullable
    @Column
    private String avatarUrl;

    @Nullable
    @Column
    private String email;

    @Nullable
    @Column
    private Long creatorAccountId;

    @Transient
    @Nullable
    private Account creatorAccount;

    public Account() {
    }

    public Account(@Nonnull final String login, @Nonnull final String password, final AccountRole role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public Account(@Nonnull final Account other) {
        this.id = other.id;
        this.d = other.d;
        this.creationDate = other.creationDate;
        this.deleted = other.deleted;
        this.login = other.login;
        this.password = other.password;
        this.role = other.role;
        this.name = other.name;
        this.avatarUrl = other.avatarUrl;
        this.email = other.email;
        this.creatorAccountId = other.creatorAccountId;
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
        return creatorAccountId;
    }

    @Override
    public void setAccountId(@Nonnull final Long accountId) {
        this.creatorAccountId = accountId;
    }

    @Nullable
    @Override
    public Account getAccount() {
        return creatorAccount;
    }

    @Override
    public void setAccount(@Nonnull final Account account) {
        this.creatorAccount = account;
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
    public Account copy() {
        return new Account(this);
    }

    @Override
    public String toString() {
        return "Account {" +
                "id=" + id +
                ", d=" + d +
                ", creationDate=" + creationDate +
                ", deleted=" + deleted +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Nonnull
    public Date getCreationDate() {
        return creationDate;
    }

    @Nonnull
    public String getLogin() {
        return login;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public AccountRole getRole() {
        return role;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Nullable
    public String getEmail() {
        return email;
    }
}
