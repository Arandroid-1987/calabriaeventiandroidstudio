package com.calabriaeventi.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.calabriaeventi.EventiFragment;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.io.SharedPreferencesManager;
import com.calabriaeventi.model.Provincia;

public class GestoreFeed {
    private final static int HOME = 0;
    private final static int REGIONE = 1;

    public static Fragment choose(int position) {
        Fragment newFragment = new EventiFragment();
        Bundle bundle = new Bundle();
        switch (position) {
            case HOME:
                Provincia home = new Provincia("Home");
                home.setCacheKey(SharedPreferencesManager.EVENTI_REGIONE_KEY);
                bundle.putSerializable(EventiFragment.PROVINCIA, home);
                break;
            case REGIONE:
                Provincia regione = new Provincia("Regione");
                regione.setCacheKey(SharedPreferencesManager.EVENTI_REGIONE_KEY);
                bundle.putSerializable(EventiFragment.PROVINCIA, regione);
                break;
            default:
                bundle.putSerializable(EventiFragment.PROVINCIA, GlobalState.province[position - 2]);
        }
        newFragment.setArguments(bundle);
        return newFragment;
    }
}
