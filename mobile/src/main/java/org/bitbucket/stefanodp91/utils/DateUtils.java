package org.bitbucket.stefanodp91.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stefano on 26/03/2016.
 */
public class DateUtils {
    public static Date convertStringToDate(String dateStr, String format, Locale locale) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Conversione di una data in una stringa.
     * Reference: http://developer.android.com/reference/java/text/SimpleDateFormat.html
     *
     * @param date   data da convertire in stringa
     * @param format formato della data (Es. "dd/MM/yyyy HH:mm")
     * @param locale Locale.ITALY
     * @return
     */
    public static String convertDateToString(Date date, String format, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        return sdf.format(date);
    }
}
