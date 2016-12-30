package ad4si2.lfp.utils.jpa;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Author:      dam <br>
 * Date:        16.12.16
 */
public abstract class CustomSetType<T> extends CustomCollectionType<Set<T>, T> {

    @Override
    protected Set<T> collectionInstance() {
        return new LinkedHashSet<>();
    }
}