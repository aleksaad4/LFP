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

            // аккаунты
            items.add(new MenuItem("Аккаунты", "accounts", "zmdi zmdi-account"));

            // футбол
            final List<MenuItem> footballSubMenu = new ArrayList<>();
            footballSubMenu.add(new MenuItem("Страны", "countries", ""));
            footballSubMenu.add(new MenuItem("Лиги", "leagues", "zmdi zmdi-star"));
            footballSubMenu.add(new MenuItem("Команды", "teams", "zmdi zmdi-accounts"));
            items.add(new MenuItem("Футбол", "football", "", footballSubMenu));

            // турниры
            items.add(new MenuItem("Турниры", "tournaments", ""));

            return items;
        } else {
            return new ArrayList<>();
        }
    }
}
