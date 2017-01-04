package ad4si2.lfp.web.controllers.admin.tounament;

import ad4si2.lfp.config.GsonConfig;
import ad4si2.lfp.data.entities.tournament.Championship;
import ad4si2.lfp.data.entities.tournament.Cup;
import ad4si2.lfp.data.entities.tournament.Tournament;
import ad4si2.lfp.data.entities.tournament.TournamentType;
import com.google.gson.*;

import java.lang.reflect.Type;

public class TournamentDeserializer implements JsonDeserializer<Tournament> {

    @Override
    public Tournament deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final Gson gson = GsonConfig.getConfiguredGson();
        final TournamentType tType = gson.fromJson(((JsonObject) jsonElement).getAsJsonObject("type"), TournamentType.class);
        switch (tType) {
            case CHAMPIONSHIP:
                return gson.fromJson(jsonObject, Championship.class);
            case CUP:
                return gson.fromJson(jsonObject, Cup.class);
            default:
                throw new UnsupportedOperationException("Can't deserialize tournament with unknown type [" + tType + "]");
        }
    }
}
