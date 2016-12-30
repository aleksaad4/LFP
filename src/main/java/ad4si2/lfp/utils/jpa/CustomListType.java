package ad4si2.lfp.utils.jpa;

import java.util.*;

/**
 * Author:      doa <br>
 * Date:        14.09.11, 13:25 <br>
 * Company:     SofIT labs <br>
 * Revision:    $ <br>
 * Description: <br>
 */
public abstract class CustomListType<T> extends CustomCollectionType<List<T>, T> {
    @Override
    protected List<T> collectionInstance() {
        return new ArrayList<>();
    }
}