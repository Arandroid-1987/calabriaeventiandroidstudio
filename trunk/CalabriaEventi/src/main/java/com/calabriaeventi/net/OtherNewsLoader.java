package com.calabriaeventi.net;

import java.util.List;

import com.calabriaeventi.core.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.core.Loading;
import com.calabriaeventi.ui.EventoAdapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ExpandableListView;

public class OtherNewsLoader extends AsyncTask<String, Void, List<Evento>> {
	private Activity context;
	private GlobalState gs;
	private ExpandableListView listView;
	private List<Evento> events;
	private Evento evento;
	private EventoAdapter adapter;

	public OtherNewsLoader(Activity context, ExpandableListView listView,
			List<Evento> events, EventoAdapter adapter) {
		this.context = context;
		this.listView = listView;
		this.events = events;
		this.adapter = adapter;
	}

	@Override
	protected void onPreExecute() {
		gs = (GlobalState) context.getApplication();
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (!events.contains(evento)) {
					evento = new Loading();
					evento.setNome("Loading");
					evento.setLuogo("Loading");
					evento.setData("Loading");
					events.add(evento);
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	@Override
	protected List<Evento> doInBackground(String... arg0) {
		List<Evento> news = gs.getOtherEvents();
		return news;
	}

	@Override
	protected void onPostExecute(List<Evento> result) {
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (events.contains(evento)) {
					events.remove(evento);
					adapter.notifyDataSetChanged();
				}
			}
		});

	}

}
