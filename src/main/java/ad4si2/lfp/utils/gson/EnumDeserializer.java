package ad4si2.lfp.utils.gson;

import com.google.gson.*;

import java.lang.reflect.Type;

public class EnumDeserializer implements JsonDeserializer<Enum> {

    @Override
    public Enum deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonPrimitive prim = (JsonPrimitive) jsonObject.get("name");
        final String enumName = prim.getAsString();
        return Enum.valueOf((Class<Enum>) typeOfT, enumName);
    }
}