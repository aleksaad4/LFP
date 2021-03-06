package ad4si2.lfp.data.entities.account;

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
@Table(name = "account")
public abstract class Account implements Serializable, IDeleted, IEntity<Long, Account>, IAccountable {

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

    @Column(nullable = false)
    private boolean blocked = false;

    @Nonnull
    @Column(nullable = false)
    private String login;

    @Nonnull
    @Column(nullable = false)
    private String password;

    @Nonnull
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

    protected Account() {
    }

    public Account(@Nonnull final String login, @Nonnull final String password, @Nonnull final AccountRole role,
                   @Nullable final String name, @Nullable final String avatarUrl, @Nullable final String email) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.email = email;
    }

    public Account(@Nonnull final String login, @Nonnull final String password, @Nonnull final AccountRole role) {
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
        this.blocked = other.blocked;
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
    public void setAccount(@Nullable final Account account) {
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

    @Nonnull
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

    public void setRole(@Nonnull final AccountRole role) {
        this.role = role;
    }

    public void setBlocked(final boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return "Account {" +
                "id=" + id +
                ", d=" + d +
                ", creationDate=" + creationDate +
                ", deleted=" + deleted +
                ", blocked=" + blocked +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", email='" + email + '\'' +
                ", creatorAccountId=" + creatorAccountId +
                '}';
    }

    public boolean isBlocked() {
        return blocked;
    }
}
