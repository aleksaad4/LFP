package ad4si2.lfp.data.entities.account;

import javax.annotation.Nonnull;
import javax.persistence.Entity;

@Entity
public class Admin extends Account {

    public Admin() {
    }

    public Admin(@Nonnull final String login, @Nonnull final String password, final AccountRole role) {
        super(login, password, role);
    }

    public Admin(@Nonnull final Account other) {
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
