package com.calabriaeventi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public interface Constants {
    String CACHE_DATE_FORMAT = "yyyyMMdd";
    SimpleDateFormat CACHE_DATE_FORMATTER = new SimpleDateFormat(CACHE_DATE_FORMAT, Locale.getDefault());
    String TODAY = CACHE_DATE_FORMATTER.format(new Date());
}
