package com.calabriaeventi.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.calabriaeventi.R;
import com.calabriaeventi.model.Evento;
import com.calabriaeventi.utils.Constants;
import com.calabriaeventi.utils.DateUtils;
import com.calabriaeventi.utils.ObservableImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class SharedPreferencesManager {
    private SharedPreferences sharedPreferences;
    private static SharedPreferencesManager instance;
    private Gson gson = new GsonBuilder().create();
    private HashSet<Evento> favorites;
    private ObservableImpl observable = new ObservableImpl();

    public final static String EVENTI_REGIONE_KEY = "EVENTI_REGIONE_KEY";
    public final static String EVENTI_COSENZA_KEY = "EVENTI_COSENZA_KEY";
    public final static String EVENTI_CATANZARO_KEY = "EVENTI_CATANZARO_KEY";
    public final static String EVENTI_REGGIO_CALABRIA_KEY = "EVENTI_REGGIO_CALABRIA_KEY";
    public final static String EVENTI_CROTONE_KEY = "EVENTI_CROTONE_KEY";
    public final static String EVENTI_VIBO_VALENTIA_KEY = "EVENTI_VIBO_VALENTIA_KEY";
    private final static String FAVORITES_EVENTS_KEY = "FAVORITES_EVENTS_KEY";

    public static SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    private SharedPreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String favoritesJson = sharedPreferences.getString(FAVORITES_EVENTS_KEY, null);
        if (favoritesJson == null) {
            this.favorites = new HashSet<>();
        } else {
            this.favorites = gson.fromJson(favoritesJson, new TypeToken<HashSet<Evento>>() {
            }.getType());
        }
    }

    public TimedObject<ArrayList<Evento>> getEventi(String key) {
        TimedObject<ArrayList<Evento>> ris = new TimedObject<>();
        ArrayList<Evento> list = new ArrayList<>();
        ris.setContent(list);
        ris.setDate("");
        String json = sharedPreferences.getString(key, null);
        if (json != null) {
            try {
                Type type = new TypeToken<TimedObject<ArrayList<Evento>>>() {
                }.getType();
                ris = gson.fromJson(json, type);
            } catch (Exception ignored) {
            }
        }
        return ris;
    }

    public void storeEventi(String key, ArrayList<Evento> eventi) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        TimedObject<ArrayList<Evento>> timedObject = new TimedObject<>(eventi);
        editor.putString(key, gson.toJson(timedObject));
        editor.apply();
    }

    public void storeFavorite(Evento e) {
        favorites.add(e);
        observable.setChanged();
        observable.notifyObservers();
        sharedPreferences.edit().putString(FAVORITES_EVENTS_KEY, gson.toJson(favorites)).apply();
    }

    public void deleteFavorite(Evento e) {
        favorites.remove(e);
        observable.setChanged();
        observable.notifyObservers();
        sharedPreferences.edit().putString(FAVORITES_EVENTS_KEY, gson.toJson(favorites)).apply();
    }

    public ArrayList<Evento> loadFavorites() {
        return new ArrayList<>(favorites);
    }

    public Observable getObservable() {
        return observable;
    }

    public boolean isFavorite(Evento evento) {
        return favorites.contains(evento);
    }
}
