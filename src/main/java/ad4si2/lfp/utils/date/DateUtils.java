package ad4si2.lfp.utils.date;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * Метод для получения пары дат ОТ начала дня и ДО конца дня
     *
     * @param date текущая дата (день)
     * @return (начало дня, конец дня)
     */
    @Nonnull
    public static Pair<Date, Date> getDayDates(@Nonnull final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);
        // begin day
        c.set(year, month, day, 0, 0, 0);
        final Date begin = c.getTime();
        // end day
        c.set(year, month, day, 23, 59, 59);
        final Date end = c.getTime();
        return new ImmutablePair<>(begin, end);
    }
}
