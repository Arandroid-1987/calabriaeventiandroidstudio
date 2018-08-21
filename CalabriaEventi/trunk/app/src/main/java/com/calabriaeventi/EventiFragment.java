package com.calabriaeventi;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.calabriaeventi.model.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.model.Provincia;
import com.calabriaeventi.net.OtherNewsLoader;
import com.calabriaeventi.ui.EventoAdapter;
import com.calabriaeventi.utils.GestoreFeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class EventiFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventoAdapter adp;
    private List<Evento> events;
    private List<Evento> ris;
    private Provincia provincia;
    private View progressBar;
    private boolean loading = false;
    private Snackbar infoSnackbar;

    private GlobalState gs;
    private Activity context;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;

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

        context = getActivity();

        recyclerView = rootView.findViewById(R.id.eventsListView);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mLayoutManager);

        events = new ArrayList<>();

        if (provincia.getNome().equals("Regione")) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                        totalItemCount = mLayoutManager.getItemCount();

                        if (!loading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                loading = true;
                                infoSnackbar = Snackbar.make(recyclerView, R.string.searching, Snackbar.LENGTH_INDEFINITE);
                                infoSnackbar.show();
                                caricaAltreNews();
                            }
                        }
                    }
                }
            });
        }

        progressBar = rootView.findViewById(R.id.progressBar);

        gs = (GlobalState) context.getApplication();
        events.clear();
        events.addAll(gs.retrieveEvents(provincia));

        adp = new EventoAdapter(context, R.layout.item_evento, events);
        recyclerView.setAdapter(adp);

        if (events.isEmpty()) {
            Parti cl = new Parti();
            cl.execute();
        } else {
            progressBar.setVisibility(View.GONE);
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
        if (query.trim().length() == 0) {
            events.clear();
            events.addAll(gs.retrieveEvents(provincia));
            adp.notifyDataSetChanged();
        } else {
            events.clear();
            for (Evento e : gs.retrieveEvents(provincia)) {
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


    class Parti extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            creaLista();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    adp = new EventoAdapter(context, R.layout.item_evento, events);
                    recyclerView.setAdapter(adp);
                }
            });
            progressBar.setVisibility(View.GONE);
        }

        private void creaLista() {
            GlobalState gs = (GlobalState) context.getApplication();
            ris = new ArrayList<>();
            ris = gs.getEvents(provincia);
            events.addAll(ris);
        }

    }

    private void caricaAltreNews() {
        Thread t = new Thread() {
            public void run() {
                OtherNewsLoader onl = new OtherNewsLoader(context);
                onl.execute();
                try {
                    ris = onl.get();
                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            events.addAll(ris);
                            if (infoSnackbar != null && infoSnackbar.isShownOrQueued()) {
                                infoSnackbar.dismiss();
                            }
                            if (!ris.isEmpty()) {
                                adp.notifyDataSetChanged();
                                loading = false;
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

}
