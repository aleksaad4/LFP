package ad4si2.lfp.utils.data;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Author:      daa
 * Date:        16.06.16
 * Company:     SofIT labs
 */
public interface IEntity<ID, T> extends Serializable {
    @Nonnull
    ID getId();

    @Nonnull
    T copy();
}
