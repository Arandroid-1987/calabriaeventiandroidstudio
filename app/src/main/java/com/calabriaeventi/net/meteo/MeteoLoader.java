package com.calabriaeventi.net.meteo;

import android.os.AsyncTask;

import com.calabriaeventi.io.AsyncCallback;
import com.calabriaeventi.model.Evento;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MeteoLoader extends AsyncTask<String, Void, Void> {
    private Evento evento;
    private AsyncCallback<String> asyncCallback;

    public MeteoLoader(Evento evento, AsyncCallback<String> asyncCallback) {
        this.evento = evento;
        this.asyncCallback = asyncCallback;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(String... arg0) {
        List<Date> date = evento.getDate();
        Date dat = new Date();
        if (date != null && date.size() > 0) {
            if (dat.before(date.get(0))) {
                dat = date.get(0);
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date nextWeek = cal.getTime();
        String meteo = "";
        if (dat.after(nextWeek)) {
            evento.setMeteo(meteo);
        } else {
            if (evento.getPlace() != null) {
                meteo = new MeteoParser().getMeteo(evento.getPlace(), dat);
                if (meteo == null) {
                    meteo = "";
                }
                evento.setMeteo(meteo);
            }
        }
        if (asyncCallback != null) {
            asyncCallback.callback(meteo);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    }

}
