package barqsoft.footballscores.commons;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by thomasthiebaud on 19/12/15.
 */
public final class Day {
    private Day() {}

    public static long getStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        return calendar.getTimeInMillis();
    }

    public static long getCurrentStart() {
        return getStart(new Date(System.currentTimeMillis()));
    }

    public static long getEnd(long start) {
        return start + 24L * 60 * 60 * 24 * 1000;
    }

    public static long getEnd(Date date) {
        return getEnd(getStart(date));
    }

    public static long getCurrentEnd() {
        return getEnd(getCurrentStart());
    }
}
