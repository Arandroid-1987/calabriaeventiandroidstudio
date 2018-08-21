package com.calabriaeventi.utils;

import com.calabriaeventi.model.Evento;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    private final static String DATE_FORMAT_JBLASA = "dd MMM";
    private final static String DATE_FORMAT_JBLASA_WITH_YEAR = DATE_FORMAT_JBLASA + " yyyy";
    private final static String DATE_FORMAT_PRINT = "EEEE dd MMMM yyyy";

    private static void setDates(Evento evento, String format) {
        String startDate = evento.getDataInizio();
        String endDate = evento.getDataFine();
        List<Date> date;
        try {
            startDate = startDate.toLowerCase(Locale.ITALIAN).trim();
            endDate = endDate.toLowerCase(Locale.ITALIAN).trim();
            if (endDate.isEmpty()) {
                endDate = startDate;
            }
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            int endYear = cal.get(Calendar.YEAR);
            int startYear = endYear;
            int currentMonth = cal.get(Calendar.MONTH);

            DateFormat df = new SimpleDateFormat(format, Locale.ITALIAN);
            Date startD = df.parse(startDate);
            cal.setTime(startD);
            int startMonth = cal.get(Calendar.MONTH);
            if (startMonth > currentMonth) {
                startYear -= 1;
            }

            startDate = startDate + " " + startYear;
            if (endDate.length() < 3) {
                endDate += " " + startDate.substring(startDate.length() - 8);
            } else {
                endDate = endDate + " " + endYear;
            }
            date = getDatasBetween(startDate, endDate);
            if (!date.isEmpty()) {
                SimpleDateFormat printFormatter = new SimpleDateFormat(DATE_FORMAT_PRINT, Locale.ITALIAN);
                evento.setDataInizio(printFormatter.format(date.get(0)));

                evento.setDataFine(printFormatter.format(date.get(date.size() - 1)));
            }
            evento.setDate(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setDates(Evento evento) {
        setDates(evento, DATE_FORMAT_JBLASA);
    }

    private static List<Date> getDatasBetween(String firstDate, String lastDate) throws ParseException {
        List<Date> date = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_JBLASA_WITH_YEAR, Locale.ITALIAN);
        Date first = sdf.parse(firstDate);
        Date last = sdf.parse(lastDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(first);
        while (first.before(last)) {
            date.add(first);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            first = cal.getTime();
        }
        date.add(last);
        return date;
    }

    public static void setDatesFromDb(Evento e) {
        setDates(e, DATE_FORMAT_PRINT);
    }
}
