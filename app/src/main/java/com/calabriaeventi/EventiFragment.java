package com.calabriaeventi;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.calabriaeventi.firebase.EventsManager;
import com.calabriaeventi.io.SharedPreferencesManager;
import com.calabriaeventi.io.TimedObject;
import com.calabriaeventi.model.Evento;
import com.calabriaeventi.model.Provincia;
import com.calabriaeventi.ui.EventoAdapter;
import com.calabriaeventi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Locale;

public class EventiFragment extends Fragment {
    private EventoAdapter adp;
    private ArrayList<Evento> events;
    private Provincia provincia;
    private SharedPreferencesManager preferencesManager;

    public final static String PROVINCIA = "Provincia";

    private AdView mAdView;

    public EventiFragment() {
        provincia = null;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args.containsKey(PROVINCIA)) {
            provincia = (Provincia) args.getSerializable(PROVINCIA);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_eventi, container,
                false);

        Activity activity = getActivity();
        if (activity != null) {

            mAdView = rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            RecyclerView recyclerView = rootView.findViewById(R.id.eventsListView);
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext()) {
                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            recyclerView.setLayoutManager(mLayoutManager);

            preferencesManager = SharedPreferencesManager.getInstance(activity);
            TimedObject<ArrayList<Evento>> object = preferencesManager.getEventi(provincia.getCacheKey());
            events = object.getContent();

            final View progressBar = rootView.findViewById(R.id.progressBar);

            adp = new EventoAdapter(activity, R.layout.item_evento, events);
            recyclerView.setAdapter(adp);

            if (!Constants.TODAY.equals(object.getDate())) {
                EventsManager.getInstance().loadEvents(provincia.getFirebaseName(), ris -> {
                    events.clear();
                    if (provincia.getNome().equals("Home")) {
                        events.addAll(EventsManager.getEventiGiornalieri(ris));
                    } else if (provincia.getNome().equals("Domani")) {
                        events.addAll(EventsManager.getEventiDomani(ris));
                    } else {
                        events.addAll(ris);
                        preferencesManager.storeEventi(provincia.getCacheKey(), events);
                    }
                    adp.applyFilter("");
                    adp.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                if (provincia.getNome().equals("Home")) {
                    ArrayList<Evento> eventiGiornalieri = EventsManager.getEventiGiornalieri(events);
                    events.clear();
                    events.addAll(eventiGiornalieri);
                    adp.applyFilter("");
                    adp.notifyDataSetChanged();
                } else if (provincia.getNome().equals("Domani")) {
                    ArrayList<Evento> eventiDomani = EventsManager.getEventiDomani(events);
                    events.clear();
                    events.addAll(eventiDomani);
                    adp.applyFilter("");
                    adp.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }
        }





        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_tab, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnSearchClickListener(v -> searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.trim().length() == 0) {
                    filterEvents(query);
                }
                return false;
            }
        }));
    }

    private void filterEvents(String query) {
        query = query.toLowerCase(Locale.getDefault());
        adp.applyFilter(query);
        adp.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_search;
    }

}
