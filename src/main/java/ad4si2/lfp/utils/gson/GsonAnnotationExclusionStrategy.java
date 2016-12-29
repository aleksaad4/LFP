package ad4si2.lfp.utils.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonAnnotationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(final FieldAttributes f) {
        return f.getAnnotation(GsonIgnore.class) != null;
    }

    @Override
    public boolean shouldSkipClass(final Class<?> clazz) {
        return false;
    }

}