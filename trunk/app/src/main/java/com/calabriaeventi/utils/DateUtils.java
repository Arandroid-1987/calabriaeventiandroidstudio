package com.calabriaeventi.utils;

import com.calabriaeventi.model.Evento;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private final static String DATE_FORMAT_PRINT = "EEEE dd MMMM yyyy";

    public static void setDates(Evento evento) {
        String startDate = evento.getStartDate();
        String endDate = evento.getEndDate();
        ArrayList<Date> date;
        try {
            date = getDatasBetween(startDate, endDate);
            if (!date.isEmpty()) {
                SimpleDateFormat printFormatter = new SimpleDateFormat(DATE_FORMAT_PRINT, Locale.ITALIAN);
                evento.setStartDate(printFormatter.format(date.get(0)));
                evento.setEndDate(printFormatter.format(date.get(date.size() - 1)));
            }
            evento.setDate(date);
        } catch (Exception ex) {
            evento.setDate(new ArrayList<Date>());
        }
    }

    private static ArrayList<Date> getDatasBetween(String firstDate, String lastDate) throws ParseException {
        ArrayList<Date> date = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.FIREBASE_DATE_FORMAT, Locale.ITALIAN);
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

}
