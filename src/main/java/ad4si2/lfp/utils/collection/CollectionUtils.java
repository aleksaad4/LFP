package ad4si2.lfp.utils.collection;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

    @SafeVarargs
    @Nonnull
    public static <T> Set<T> asSet(@Nonnull final T... t) {
        final HashSet<T> set = new HashSet<>();
        Collections.addAll(set, t);
        return set;
    }
}
