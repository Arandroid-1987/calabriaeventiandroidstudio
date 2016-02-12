package com.calabriaeventi;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.calabriaeventi.R;
import com.calabriaeventi.core.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.ui.EventoAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class PreferitiFragment extends Fragment implements
		Observer {
	private ExpandableListView listView;
	public EventoAdapter adp;
	public List<Evento> events;
	public Evento e;
	private LinearLayout adsLL;
	private View progressBar;

	private View rootView;
	private Activity context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rootView = inflater.inflate(R.layout.activity_eventi_screen,
				container, false);
		
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

		GlobalState gs = (GlobalState) context.getApplication();
		events = gs.loadFavorites();
		gs.getObservable().addObserver(this);
		listView = (ExpandableListView) rootView.findViewById(R.id.eventsListView);
		listView.setGroupIndicator(null);
		
		progressBar = rootView.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);

		adp = new EventoAdapter(context, R.layout.evento, events);

		listView.setAdapter(adp);

		return rootView;
	}
	
	public void reload(){
		GlobalState gs = (GlobalState) context.getApplication();
		events = gs.loadFavorites();
		adp = new EventoAdapter(context, R.layout.evento, events);
		listView.setAdapter(adp);
	}

	@Override
	public void update(Observable observable, Object data) {
		reload();
	}
	

}
