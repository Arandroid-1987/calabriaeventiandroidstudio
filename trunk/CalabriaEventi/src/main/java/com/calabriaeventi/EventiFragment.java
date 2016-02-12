package com.calabriaeventi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.calabriaeventi.R;
import com.calabriaeventi.core.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.core.Loading;
import com.calabriaeventi.net.OtherNewsLoader;
import com.calabriaeventi.ui.EventoAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class EventiFragment extends Fragment implements OnScrollListener {
	private ExpandableListView listView;
	private EventoAdapter adp;
	private List<Evento> events;
	private List<Evento> ris;
	private int feed;
	private LinearLayout adsLL;
	private View progressBar;

	private GlobalState gs;
	private View rootView;
	private Activity context;

	private int preLast;
	private Evento preLastEvento;
	
	public final static String FEED = "Feed";

	public EventiFragment() {
		feed = -1;
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		if(args.containsKey(FEED)){
			feed = args.getInt(FEED);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.tab, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);
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
				listView.setOnScrollListener(null);
			}
		});
	}

	protected void filterEvents(String query) {
		query = query.toLowerCase(Locale.getDefault());
		if (query.trim().length() == 0) {
			events.clear();
			events.addAll(gs.retrieveEvents(feed));
			adp.notifyDataSetChanged();
			if (feed == -1) {
				listView.setOnScrollListener(this);
			}
		} else {
			events.clear();
			for (Evento e : gs.retrieveEvents(feed)) {
				boolean add = false;
				StringTokenizer st = new StringTokenizer(query);
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (e.getNome().toLowerCase(Locale.getDefault())
							.contains(token)
							|| e.getLuogo().toLowerCase(Locale.getDefault())
									.contains(token)
							|| e.getData().toLowerCase(Locale.getDefault())
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
		if (id == R.id.action_search) {
			return true;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = inflater.inflate(R.layout.activity_eventi_screen, container,
				false);

		context = getActivity();

		adsLL = (LinearLayout) rootView.findViewById(R.id.adsLinearLayout);
		AdView adView = new AdView(context);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId("ca-app-pub-2997805148414323/9432860494");
		// Effettuiamo la lookup della ViewGroup che conterrà il nostro banner
		// Nel nostro caso è un LinearLayout con id linearLayout
		// Aggiungiamo la view adView al LinearLayout
		adsLL.addView(adView);
		// Richiediamo un nuovo banner al server di AdMod
		AdRequest adRequest = new AdRequest.Builder().build();

		// Start loading the ad in the background.
		adView.loadAd(adRequest);

		listView = (ExpandableListView) rootView
				.findViewById(R.id.eventsListView);
		listView.setGroupIndicator(null);

		events = new ArrayList<Evento>();

		if (feed == -1) {
			listView.setOnScrollListener(this);
		}

		progressBar = rootView.findViewById(R.id.progressBar);

		gs = (GlobalState) context.getApplication();
		events.clear();
		events.addAll(gs.retrieveEvents(feed));

		adp = new EventoAdapter(context, R.layout.evento, events);
		listView.setAdapter(adp);

		if (events.isEmpty()) {
			Parti cl = new Parti();
			cl.execute();
		} else {
			progressBar.setVisibility(View.GONE);
		}

		return rootView;

	}

	class Parti extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// p.show();
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
					adp = new EventoAdapter(context, R.layout.evento, events);
					listView.setAdapter(adp);
				}
			});
			progressBar.setVisibility(View.GONE);
		}

		private void creaLista() {
			GlobalState gs = (GlobalState) context.getApplication();
			ris = new ArrayList<Evento>();
			ris = gs.getEvents(feed);
			for (Evento evento : ris) {
				events.add(evento);
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView lw, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (totalItemCount > 0 && events.size() > 0) {
			switch (lw.getId()) {
			case R.id.eventsListView:
				final int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount) {
					Evento last = events.get(events.size() - 1);
					if (preLast != lastItem && !(last instanceof Loading)
							&& last != null && !(last.equals(preLastEvento))) {
						preLast = lastItem;
						preLastEvento = last;
						caricaAltreNews();
					}
				}
			}
		}
	}

	private void caricaAltreNews() {
		Thread t = new Thread() {
			public void run() {
				OtherNewsLoader onl = new OtherNewsLoader(context, listView,
						events, adp);
				onl.execute("");
				try {
					ris = onl.get();
					context.runOnUiThread(new Runnable() {
						public void run() {
							events.addAll(ris);
							adp.notifyDataSetChanged();
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
