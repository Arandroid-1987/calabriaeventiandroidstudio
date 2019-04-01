package com.calabriaeventi.io;

import com.calabriaeventi.utils.Constants;

import java.io.Serializable;

/**
 * Created by MeringoloRo on 01/12/2017.
 */

public class TimedObject<T extends Serializable> implements Serializable {
    private String date;
    private T content;

    TimedObject() {
        this.date = Constants.TODAY;
    }

    TimedObject(T content) {
        this.date = Constants.TODAY;
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
