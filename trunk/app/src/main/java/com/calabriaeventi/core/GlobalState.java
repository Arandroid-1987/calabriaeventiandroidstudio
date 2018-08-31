package com.calabriaeventi.core;

import android.app.Application;
import androidx.multidex.MultiDex;

import com.calabriaeventi.R;
import com.calabriaeventi.io.SharedPreferencesManager;
import com.calabriaeventi.model.Provincia;

public class GlobalState extends Application {
    public static Provincia[] province = new Provincia[5];

    static {
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

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
