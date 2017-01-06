package ad4si2.lfp.config;

import ad4si2.lfp.data.entities.account.Account;
import ad4si2.lfp.data.entities.account.AccountRole;
import ad4si2.lfp.data.entities.tour.TourStatus;
import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.data.entities.tournament.TournamentStatus;
import ad4si2.lfp.data.entities.tournament.TournamentType;
import ad4si2.lfp.utils.gson.EnumDeserializer;
import ad4si2.lfp.utils.gson.EnumSerializer;
import ad4si2.lfp.utils.gson.GsonAnnotationExclusionStrategy;
import ad4si2.lfp.web.controllers.admin.account.AccountDeserializer;
import ad4si2.lfp.web.controllers.admin.tounament.TournamentDeserializer;
import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class GsonConfig {

    @Bean(name = "gsonConfiguration")
    public static Gson getConfiguredGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new GsonAnnotationExclusionStrategy())
                .setPrettyPrinting()
                .registerTypeAdapter(AccountRole.class, new EnumSerializer())
                .registerTypeAdapter(AccountRole.class, new EnumDeserializer())
                .registerTypeAdapter(TournamentStatus.class, new EnumSerializer())
                .registerTypeAdapter(TournamentStatus.class, new EnumDeserializer())
                .registerTypeAdapter(TournamentType.class, new EnumSerializer())
                .registerTypeAdapter(TournamentType.class, new EnumDeserializer())
                .registerTypeAdapter(TourStatus.class, new EnumSerializer())
                .registerTypeAdapter(TourStatus.class, new EnumDeserializer())
                .registerTypeAdapter(Account.class, new AccountDeserializer())
                .registerTypeAdapter(Tournament.class, new TournamentDeserializer())
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) -> new JsonPrimitive(src.getTime()))
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsLong()))
                .create();
    }
}