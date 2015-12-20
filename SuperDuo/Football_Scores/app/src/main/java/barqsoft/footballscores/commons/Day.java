package barqsoft.footballscores.commons;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by thomasthiebaud on 19/12/15.
 */
public final class Day {
    private static final String TAG = Day.class.getSimpleName();

    private Day() {}

    public static long getStart(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTimeInMillis();
    }

    public static long getStart(Date date) {
        return getStart(date.getTime());
    }

    public static long getCurrentStart() {
        return getStart(new Date(System.currentTimeMillis()));
    }

    public static long getEnd(long start) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(start);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    public static long getEnd(Date date) {
        return getEnd(date.getTime());
    }

    public static long getCurrentEnd() {
        return getEnd(new Date(System.currentTimeMillis()));
    }
}
