package ad4si2.lfp.utils.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class EnumSerializer implements JsonSerializer<Enum> {

    public JsonElement serialize(final Enum anEnum, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject jo = new JsonObject();
        final Field[] fields = anEnum.getClass().getDeclaredFields();
        for (int i = anEnum.getClass().getEnumConstants().length; i < fields.length - 1; i++) {
            try {
                final Field field = fields[i];
                field.setAccessible(true);
                if (field.get(anEnum) != null) {
                    jo.addProperty(field.getName(), field.get(anEnum).toString());
                }
            } catch (final IllegalAccessException e) {
                // do nothing
            }
        }
        jo.addProperty("name", anEnum.name());
        jo.addProperty("id", anEnum.ordinal());
        return jo;
    }
}