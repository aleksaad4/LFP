package ad4si2.lfp.data.entities.account;

import javax.annotation.Nonnull;
import javax.persistence.Entity;

@Entity
public class Player extends Account {

    public Player() {
    }

    public Player(@Nonnull final String login, @Nonnull final String password, final AccountRole role) {
        super(login, password, role);
    }

    public Player(@Nonnull final Account other) {
        super(other);
    }

    @Nonnull
    @Override
    public Player copy() {
        return new Player(this);
    }

    @Override
    public String toString() {
        return "Player {} " + super.toString();
    }
}
