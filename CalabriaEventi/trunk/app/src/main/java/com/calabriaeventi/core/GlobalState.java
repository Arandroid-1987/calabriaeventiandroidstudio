package com.calabriaeventi.core;

import android.app.Application;
import android.util.SparseArray;

import com.calabriaeventi.R;
import com.calabriaeventi.io.SharedPreferencesManager;
import com.calabriaeventi.model.Evento;
import com.calabriaeventi.model.Provincia;
import com.calabriaeventi.net.JBlasaParser;
import com.calabriaeventi.utils.Constants;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GlobalState extends Application {
    private LinkedList<Evento> eventi;
    private LinkedList<Evento> eventiGiornalieri;
    private SparseArray<LinkedList<Evento>> eventiProvince;
    private int k;
    private SharedPreferencesManager sharedPreferencesManager;
    public static Provincia [] province = new Provincia[5];

    static{
        Provincia provincia = new Provincia();
        provincia.setNome("Cosenza");
        provincia.setCacheKey(SharedPreferencesManager.EVENTI_COSENZA_KEY);
        provincia.setFeed(R.string.feed_eventi_cosenza);

        province[0] = provincia;

        provincia = new Provincia();
        provincia.setNome("Crotone");
        provincia.setCacheKey(SharedPreferencesManager.EVENTI_CROTONE_KEY);
        provincia.setFeed(R.string.feed_eventi_crotone);

        province[1] = provincia;

        provincia = new Provincia();
        provincia.setNome("Catanzaro");
        provincia.setCacheKey(SharedPreferencesManager.EVENTI_CATANZARO_KEY);
        provincia.setFeed(R.string.feed_eventi_catanzaro);

        province[2] = provincia;

        provincia = new Provincia();
        provincia.setNome("Vibo Valentia");
        provincia.setCacheKey(SharedPreferencesManager.EVENTI_VIBO_VALENTIA_KEY);
        provincia.setFeed(R.string.feed_eventi_vibo_valentia);

        province[3] = provincia;

        provincia = new Provincia();
        provincia.setNome("Reggio Calabria");
        provincia.setCacheKey(SharedPreferencesManager.EVENTI_REGGIO_CALABRIA_KEY);
        provincia.setFeed(R.string.feed_eventi_reggio_calabria);

        province[4] = provincia;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getK() {
        return k;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getApplicationContext());
        eventi = sharedPreferencesManager.getEventi(SharedPreferencesManager.EVENTI_REGIONE_KEY);
        calculateEventiGiornalieri();
        initEventiProvince();
    }

    private void initEventiProvince() {
        eventiProvince = new SparseArray<>();
        for (Provincia provincia : province) {
            eventiProvince.put(provincia.getFeed(), sharedPreferencesManager.getEventi(provincia.getCacheKey()));
        }
    }

    public LinkedList<Evento> getEvents() {
        if (eventi.isEmpty()) {
            eventi = new JBlasaParser().read(getString(R.string.feed_eventi),
                    this);
            sharedPreferencesManager.storeEventi(SharedPreferencesManager.EVENTI_REGIONE_KEY, eventi);
            eventiGiornalieri = getEventiGiornalieri();
        }
        return eventi;
    }

    public LinkedList<Evento> retrieveEvents(Provincia provincia) {
        if (provincia.getNome().equals("Home")) {
            return eventiGiornalieri;
        }
        if (provincia.getNome().equals("Regione")) {
            return eventi;
        }
        LinkedList<Evento> ris = eventiProvince.get(provincia.getFeed());
        if (ris == null || ris.isEmpty()) {
            ris = new LinkedList<>();
            eventiProvince.put(provincia.getFeed(), ris);
        }
        return ris;

    }

    public LinkedList<Evento> getEvents(Provincia provincia) {
        if (provincia.getNome().equals("Home")) {
            return getEventiGiornalieri();
        }
        if (provincia.getNome().equals("Regione")) {
            return getEvents();
        }
        LinkedList<Evento> ris = eventiProvince.get(provincia.getFeed());
        if (ris == null || ris.isEmpty()) {
            ris = new JBlasaParser().read(getString(provincia.getFeed()), this);
            eventiProvince.put(provincia.getFeed(), ris);
            sharedPreferencesManager.storeEventi(provincia.getCacheKey(), ris);
        }
        return ris;

    }

    public LinkedList<Evento> getOtherEvents() {
        LinkedList<Evento> otherEvents = new JBlasaParser().readOther(
                getString(R.string.feed_eventi), eventi.size(),
                eventi.size() + 10, this);
        eventi.addAll(otherEvents);
        sharedPreferencesManager.storeEventi(SharedPreferencesManager.EVENTI_REGIONE_KEY, eventi);
        return otherEvents;
    }

    public LinkedList<Evento> getEventiGiornalieri() {
        if (eventi.isEmpty()) {
            getEvents();
        }
        calculateEventiGiornalieri();
        return eventiGiornalieri;
    }

    private void calculateEventiGiornalieri() {
        eventiGiornalieri = new LinkedList<>();
        for (Evento evento : eventi) {
            List<Date> date = evento.getDate();
            for (Date data : date) {
                String dateStr = Constants.CACHE_DATE_FORMATTER.format(data);
                if (dateStr.equals(Constants.TODAY)) {
                    eventiGiornalieri.add(evento);
                    break;
                }
            }
        }
    }
}
