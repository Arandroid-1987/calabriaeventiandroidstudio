package com.calabriaeventi.model;

import com.calabriaeventi.io.AsyncCallback;
import com.calabriaeventi.net.meteo.MeteoLoader;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import androidx.annotation.NonNull;

public class Evento implements Serializable, Filterable, Comparable<Evento> {
    private static final long serialVersionUID = 6891277652233297417L;
    public static final String START_DATE_KEY = "startDate";
    private String title;
    private String startDate;
    private String endDate;
    private String parseDate;
    private String place;
    private String description;
    private String imgUrl;
    private String primaryKey;
    private ArrayList<Date> date;
    private String meteo;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Evento() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMeteo(String meteo) {
        this.meteo = meteo;
    }

    @Exclude
    public String getMeteo(AsyncCallback<String> asyncCallback) {
        if (meteo == null && asyncCallback != null) {
            new MeteoLoader(this, asyncCallback).execute();
        }
        return meteo;
    }

    public String getDescription() {
        return description;
    }

    public String getParseDate() {
        return parseDate;
    }

    public void setParseDate(String parseDate) {
        this.parseDate = parseDate;
    }

    @Exclude
    public ArrayList<Date> getDate() {
        return date;
    }

    public void setDate(ArrayList<Date> date) {
        this.date = date;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Evento other = (Evento) obj;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (title == null) {
            return other.title == null;
        } else return title.equals(other.title);
    }

    @Override
    @NonNull
    public String toString() {
        return title + "\n" +
                startDate + "\n" +
                place;
    }

    @Override
    public boolean isCompliant(String filter) {
        if (filter.isEmpty()) return true;
        boolean compliant = false;
        StringTokenizer st = new StringTokenizer(filter);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (title.toLowerCase(Locale.getDefault()).contains(token)
                    || place.toLowerCase(Locale.getDefault()).contains(token)
                    || startDate.toLowerCase(Locale.getDefault()).contains(token)
                    || endDate.toLowerCase(Locale.getDefault()).contains(token)
                    || description.toLowerCase(Locale.getDefault()).contains(token)) {
                compliant = true;
                break;
            }
        }
        return compliant;
    }

    @Override
    public int compareTo(@NonNull Evento other) {
        Date myStartDate = new Date();
        Date otherStartDate = new Date();
        if(date.size() > 0){
            myStartDate = date.get(0);
        }
        if (other.getDate().size() > 0) {
            otherStartDate = other.getDate().get(0);
        }
        return myStartDate.compareTo(otherStartDate);
    }
}
