package ad4si2.lfp.web.controllers.admin.account;

import ad4si2.lfp.config.GsonConfig;
import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.AccountRole;
import ad4si2.lfp.data.entities.account.Admin;
import ad4si2.lfp.data.entities.account.Player;
import com.google.gson.*;

import java.lang.reflect.Type;

public class AccountDeserializer implements JsonDeserializer<Account> {

    @Override
    public Account deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final Gson gson = GsonConfig.getConfiguredGson();
        final AccountRole role = gson.fromJson(((JsonObject) jsonElement).getAsJsonObject("role"), AccountRole.class);
        switch (role) {
            case ADMIN:
                return gson.fromJson(jsonObject, Admin.class);
            case PLAYER:
                return gson.fromJson(jsonObject, Player.class);
            default:
                throw new UnsupportedOperationException("Can't deserialize account with unknown role [" + role + "]");
        }
    }
}
