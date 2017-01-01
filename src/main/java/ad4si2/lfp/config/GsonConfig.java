package ad4si2.lfp.config;

import ad4si2.lfp.data.entities.account.AccountRole;
import ad4si2.lfp.utils.gson.EnumDeserializer;
import ad4si2.lfp.utils.gson.EnumSerializer;
import ad4si2.lfp.utils.gson.GsonAnnotationExclusionStrategy;
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
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) -> new JsonPrimitive(src.getTime()))
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsLong()))
                .create();
    }
}