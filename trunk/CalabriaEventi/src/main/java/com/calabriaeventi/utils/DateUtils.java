package com.calabriaeventi.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class DateUtils {

    public static List<Date> getDatas(String dataString) {
        List<Date> date = new LinkedList<Date>();
        try {
            dataString = dataString.toLowerCase(Locale.ITALIAN).trim();
            StringTokenizer st = new StringTokenizer(dataString, "-");
            String startDate = st.nextToken().trim();
            String endDate = startDate;
            if (st.hasMoreTokens()) {
                endDate = st.nextToken().trim();
            }
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            int endYear = cal.get(Calendar.YEAR);
            int startYear = endYear;
            int currentMonth = cal.get(Calendar.MONTH);

            DateFormat df = new SimpleDateFormat("ddMMM", Locale.ITALIAN);
            Date startD = df.parse(startDate);
            cal.setTime(startD);
            int startMonth = cal.get(Calendar.MONTH);
            if (startMonth > currentMonth) {
                startYear -= 1;
            }

            startDate = startDate + " " + startYear;
            if (endDate.length() < 3) {
                endDate += startDate.substring(startDate.length() - 8);
            } else {
                endDate = endDate + " " + endYear;
            }
            date = getDatasBetween(startDate, endDate, "ddMMM yyyy");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return date;
    }

    private static List<Date> getDatasBetween(String firstDate,
                                              String lastDate, String dateFormat) throws ParseException {
        List<Date> date = new LinkedList<Date>();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ITALIAN);
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
