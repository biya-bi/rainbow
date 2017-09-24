package org.rainbow.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Biya-Bi
 */
public final class DateTime {

    public static Timestamp getCurrentUtcTimestamp() {
        final long time = System.currentTimeMillis();
        return new Timestamp(time
                - Calendar.getInstance().getTimeZone().getOffset(time));
    }

    public static Date getCurrentUtcDate() {
        final long time = System.currentTimeMillis();
        return new Date(time
                - Calendar.getInstance().getTimeZone().getOffset(time));
    }

    public static Date toUtcDate(Date date) {
        long time = date.getTime();
        return new Date(time
                - Calendar.getInstance().getTimeZone().getOffset(time));
    }

    public static Date fromUtcDate(Date date) {
        long time = date.getTime();
        return new Date(time
                + Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis()));
    }

    public static Date toDate(String s) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(s);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
