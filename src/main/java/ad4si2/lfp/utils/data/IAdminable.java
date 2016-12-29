package ad4si2.lfp.utils.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

public interface IAdminable {

    @Nullable
    Long getAdminId();

    void setAdminId(Long adminId);

    @Nullable
    Date getD();

    void setD(@Nonnull final Date d);
}
