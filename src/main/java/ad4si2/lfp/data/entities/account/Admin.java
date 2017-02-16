package ad4si2.lfp.data.entities.account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;

@Entity
public class Admin extends Account {

    protected Admin() {
    }

    public Admin(@Nonnull final String login, @Nonnull final String password, @Nullable final String name, @Nullable final String avatarUrl, @Nullable final String email) {
        super(login, password, AccountRole.ADMIN, name, avatarUrl, email);
    }

    public Admin(@Nonnull final String login, @Nonnull final String password) {
        super(login, password, AccountRole.ADMIN);
    }

    public Admin(@Nonnull final Admin other) {
        super(other);
    }

    @Nonnull
    @Override
    public Admin copy() {
        return new Admin(this);
    }

    @Override
    public String toString() {
        return "Admin {} " + super.toString();
    }
}
