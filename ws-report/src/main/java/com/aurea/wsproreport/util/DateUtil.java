package com.aurea.wsproreport.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.aurea.wsproreport.logger.WsLogger;

public class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    private DateUtil() {
        throw new UnsupportedOperationException("This class can not be instantiated!");
    }

    public static Calendar getCalendar(String date) {
        Calendar calendar = getTruncCalendar();
        try {
            Date day = sdf.parse(date.trim());
            calendar.setTime(day);
        } catch (ParseException e) {
            WsLogger.error(DateUtil.class, e.getMessage());
            calendar.set(Calendar.YEAR, 1900);
        }
        return calendar;
    }

    public static Calendar getCalendar(String date, String pattern) {
        Calendar calendar = getTruncCalendar();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date day = sdf.parse(date.trim());
            calendar.setTime(day);
        } catch (ParseException e) {
            WsLogger.error(DateUtil.class, e.getMessage());
            calendar.set(Calendar.YEAR, 1900);
        }
        return calendar;
    }

    public static Calendar getLastWorkDay() {
        Calendar calendar = getTruncCalendar();
        calendar.add(Calendar.DATE, -1);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, -2);
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        return calendar;
    }

    public static Calendar getStartWorkDayOfThisWeek() {
        Calendar calendar = getTruncCalendar();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        return calendar;
    }

    public static Calendar getStartWorkDayOfLastWeek() {
        Calendar calendar = getStartWorkDayOfThisWeek();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        return calendar;
    }

    public static boolean equalsToString(Calendar calendar, String testDate) {
        if (StringUtil.nvlOrEmpty(testDate.trim(), true)) {
            return false;
        }
        Calendar testCalendar = getCalendar(testDate);
        return equal(testCalendar, calendar);
    }

    public static String formatDate(Calendar calendar) {
        StringBuilder ret = new StringBuilder();
        ret.append(calendar.get(Calendar.YEAR)).append("-");
        ret.append(calendar.get(Calendar.MONTH) + 1).append("-");
        ret.append(calendar.get(Calendar.DAY_OF_MONTH));
        return ret.toString();
    }

    public static Calendar getTruncCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar;
    }

    public static boolean betweenOrEqualAny(Calendar testDate, Calendar startDate, Calendar endDate) {
        if (equal(testDate, startDate)) {
            return true;
        }
        if (equal(testDate, endDate)) {
            return true;
        }
        return (testDate.after(startDate) && testDate.before(endDate));
    }

    public static boolean equal(Calendar calendar1, Calendar calendar2) {
        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }
}
