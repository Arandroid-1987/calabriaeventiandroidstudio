package com.calabriaeventi;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.calabriaeventi.firebase.EventsManager;
import com.calabriaeventi.io.AsyncCallback;
import com.calabriaeventi.io.SharedPreferencesManager;
import com.calabriaeventi.io.TimedObject;
import com.calabriaeventi.model.Evento;
import com.calabriaeventi.model.Provincia;
import com.calabriaeventi.ui.EventoAdapter;
import com.calabriaeventi.utils.Constants;

import java.util.ArrayList;
import java.util.Locale;

public class EventiFragment extends Fragment {
    private EventoAdapter adp;
    private ArrayList<Evento> events;
    private Provincia provincia;
    private SharedPreferencesManager preferencesManager;

    public final static String PROVINCIA = "Provincia";

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
                EventsManager.getInstance().loadEvents(provincia.getFirebaseName(), new AsyncCallback<ArrayList<Evento>>() {
                    @Override
                    public void callback(ArrayList<Evento> ris) {
                        events.clear();
                        if (provincia.getNome().equals("Home")) {
                            events.addAll(EventsManager.getEventiGiornalieri(ris));
                        } else {
                            events.addAll(ris);
                            preferencesManager.storeEventi(provincia.getCacheKey(), events);
                        }
                        adp.applyFilter("");
                        adp.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                if (provincia.getNome().equals("Home")) {
                    ArrayList<Evento> eventiGiornalieri = EventsManager.getEventiGiornalieri(events);
                    events.clear();
                    events.addAll(eventiGiornalieri);
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
        searchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchView.setOnQueryTextListener(new OnQueryTextListener() {

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
        adp.applyFilter(query);
        adp.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_search;
    }

}
