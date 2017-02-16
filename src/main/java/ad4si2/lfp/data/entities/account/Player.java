package ad4si2.lfp.data.entities.account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Entity;

@Entity
public class Player extends Account {

    protected Player() {
    }

    public Player(@Nonnull final String login, @Nonnull final String password, @Nullable final String name, @Nullable final String avatarUrl, @Nullable final String email) {
        super(login, password, AccountRole.PLAYER, name, avatarUrl, email);
    }

    public Player(@Nonnull final String login, @Nonnull final String password) {
        super(login, password, AccountRole.PLAYER);
    }

    public Player(@Nonnull final Player other) {
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
