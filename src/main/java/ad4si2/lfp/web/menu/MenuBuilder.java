package ad4si2.lfp.web.menu;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.AccountRole;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuBuilder {

    @Nonnull
    public List<MenuItem> getMenu(@Nonnull final Account account) {
        if (account.getRole() == AccountRole.ADMIN) {
            final List<MenuItem> items = new ArrayList<>();

            items.add(new MenuItem("Аккаунты", "accounts", "fa fa-history"));
            items.add(new MenuItem("Команды", "teams", "fa fa-history"));

            return items;
        } else {
            return new ArrayList<>();
        }
    }
}
