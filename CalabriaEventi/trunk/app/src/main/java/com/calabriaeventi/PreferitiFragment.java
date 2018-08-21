package com.calabriaeventi;

import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.calabriaeventi.io.SharedPreferencesManager;
import com.calabriaeventi.model.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.ui.EventoAdapter;

public class PreferitiFragment extends Fragment implements Observer {
    private RecyclerView recyclerView;
    public EventoAdapter adp;
    public List<Evento> events;
    public Evento e;

    private Activity context;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_eventi,
                container, false);

        context = getActivity();

        if (context != null) {
            sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
            events = sharedPreferencesManager.loadFavorites();
            sharedPreferencesManager.getObservable().addObserver(this);
        }

        recyclerView = rootView.findViewById(R.id.eventsListView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mLayoutManager);

        View progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        adp = new EventoAdapter(context, R.layout.item_evento, events);

        recyclerView.setAdapter(adp);

        return rootView;
    }

    public void reload() {
        events = sharedPreferencesManager.loadFavorites();
        adp = new EventoAdapter(context, R.layout.item_evento, events);
        recyclerView.setAdapter(adp);
    }

    @Override
    public void update(Observable observable, Object data) {
        reload();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_tab, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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
                });
            }
        });
    }

    protected void filterEvents(String query) {
        query = query.toLowerCase(Locale.getDefault());
        if (query.trim().length() == 0) {
            events.clear();
            events.addAll(sharedPreferencesManager.loadFavorites());
            adp.notifyDataSetChanged();
        } else {
            events.clear();
            for (Evento e : sharedPreferencesManager.loadFavorites()) {
                boolean add = false;
                StringTokenizer st = new StringTokenizer(query);
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (e.getNome().toLowerCase(Locale.getDefault())
                            .contains(token)
                            || e.getLuogo().toLowerCase(Locale.getDefault())
                            .contains(token)
                            || e.getDataInizio().toLowerCase(Locale.getDefault())
                            .contains(token)
                            || e.getDataFine().toLowerCase(Locale.getDefault())
                            .contains(token)
                            || e.getDescrizione()
                            .toLowerCase(Locale.getDefault())
                            .contains(token)) {
                        add = true;
                        break;
                    }
                }
                if (add) {
                    events.add(e);
                }
            }
            adp.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_search;
    }

}
