package de.sandkastenliga.resultserver.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date resetToStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date resetToStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return resetToStartOfDay(cal);
    }


}
