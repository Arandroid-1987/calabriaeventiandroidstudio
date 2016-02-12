package com.calabriaeventi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.calabriaeventi.R;
import com.calabriaeventi.core.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.ui.EventoGridAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class HomeFragment extends Fragment {
	private LinearLayout adsLL;

	private View rootView;
	private Activity context;
	private List<Evento> events1;
	private List<Evento> events2;
	private List<Evento> ris;
	private ListView listView1;
	private ListView listView2;
	private ListAdapter adp1;
	private ListAdapter adp2;
	private View progressBar;

	private View clickSource = null;
	private View touchSource = null;

	private int offset = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = inflater.inflate(R.layout.activity_home_screen, container,
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

		listView1 = (ListView) rootView.findViewById(R.id.listView1);
		listView2 = (ListView) rootView.findViewById(R.id.listView2);
		
		clickSource = null;
		touchSource = null;

		listView1.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (touchSource == null) {
					touchSource = v;
				}
				if (v == touchSource) {
					listView2.dispatchTouchEvent(event);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						clickSource = v;
						touchSource = null;
					}
				}

				return false;
			}
		});

		listView2.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (touchSource == null) {
					touchSource = v;
				}
				if (v == touchSource) {
					listView1.dispatchTouchEvent(event);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						clickSource = v;
						touchSource = null;
					}
				}

				return false;
			}
		});

		listView1.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (view == clickSource) {
					listView2.setSelectionFromTop(firstVisibleItem, view
							.getChildAt(0).getTop() + offset);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
		});

		progressBar = rootView.findViewById(R.id.progressBar);

		events1 = new ArrayList<Evento>();
		events2 = new ArrayList<Evento>();

		if (events1.isEmpty() && events2.isEmpty()) {

			Parti cl = new Parti();
			cl.execute();
		}
		else{
			progressBar.setVisibility(View.GONE);
		}
		
		

		return rootView;
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
					adp1 = new EventoGridAdapter(context, events1);
					adp2 = new EventoGridAdapter(context, events2);
					listView1.setAdapter(adp1);
					listView2.setAdapter(adp2);
				}
			});
			progressBar.setVisibility(View.GONE);
		}

		private void creaLista() {
			GlobalState gs = (GlobalState) context.getApplication();
			ris = new ArrayList<Evento>();
			ris = gs.getEventiGiornalieri();
			int k = 0;
			for (Evento evento : ris) {
				if (k % 2 == 0) {
					events1.add(evento);
				} else {
					events2.add(evento);
				}
				k++;
			}
		}
	}
	
	@Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && listView1 != null && listView2 != null) {
        	listView1.setOnTouchListener(new View.OnTouchListener() {
    			@Override
    			public boolean onTouch(View v, MotionEvent event) {
    				v.performClick();
    				if (touchSource == null) {
    					touchSource = v;
    				}
    				if (v == touchSource) {
    					listView2.dispatchTouchEvent(event);
    					if (event.getAction() == MotionEvent.ACTION_UP) {
    						clickSource = v;
    						touchSource = null;
    					}
    				}

    				return false;
    			}
    		});

    		listView2.setOnTouchListener(new View.OnTouchListener() {
    			@Override
    			public boolean onTouch(View v, MotionEvent event) {
    				v.performClick();
    				if (touchSource == null) {
    					touchSource = v;
    				}
    				if (v == touchSource && listView1 != null) {
    					listView1.dispatchTouchEvent(event);
    					if (event.getAction() == MotionEvent.ACTION_UP) {
    						clickSource = v;
    						touchSource = null;
    					}
    				}

    				return false;
    			}
    		});

    		listView1.setOnScrollListener(new OnScrollListener() {
    			@Override
    			public void onScroll(AbsListView view, int firstVisibleItem,
    					int visibleItemCount, int totalItemCount) {
    				if (view == clickSource && listView2!=null && view != null) {
    					listView2.setSelectionFromTop(firstVisibleItem, view
    							.getChildAt(0).getTop() + offset);
    				}
    			}

    			@Override
    			public void onScrollStateChanged(AbsListView view, int scrollState) {
    			}
    		});
        }
    }

}
