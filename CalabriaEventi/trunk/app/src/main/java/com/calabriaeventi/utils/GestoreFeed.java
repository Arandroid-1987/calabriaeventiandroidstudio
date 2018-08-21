package com.calabriaeventi.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.calabriaeventi.EventiFragment;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.model.Provincia;

public class GestoreFeed {
    private final static int HOME = 0;
    private final static int REGIONE = 1;

    public static Fragment choose(int position) {
        Fragment newFragment = new EventiFragment();
        Bundle bundle = new Bundle();
        switch (position) {
            case HOME:
                bundle.putSerializable(EventiFragment.PROVINCIA, new Provincia("Home"));
                break;
            case REGIONE:
                bundle.putSerializable(EventiFragment.PROVINCIA, new Provincia("Regione"));
                break;
            default:
                bundle.putSerializable(EventiFragment.PROVINCIA, GlobalState.province[position - 2]);
        }
        newFragment.setArguments(bundle);
        return newFragment;
    }
}
