package com.calabriaeventi.net;

import android.app.Activity;
import android.os.AsyncTask;

import com.calabriaeventi.model.Evento;
import com.calabriaeventi.core.GlobalState;

import java.util.List;

public class OtherNewsLoader extends AsyncTask<Void, Void, List<Evento>> {
    private GlobalState gs;

    public OtherNewsLoader(Activity context) {
        gs = (GlobalState) context.getApplication();
    }

    @Override
    protected List<Evento> doInBackground(Void... voids) {
        return gs.getOtherEvents();
    }

}
