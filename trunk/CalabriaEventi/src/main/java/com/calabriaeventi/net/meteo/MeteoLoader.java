package com.calabriaeventi.net.meteo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.calabriaeventi.core.Evento;

import android.os.AsyncTask;

public class MeteoLoader extends AsyncTask<String, Void, Void> {
	private Evento evento;

	public MeteoLoader(Evento evento) {
		this.evento = evento;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected Void doInBackground(String... arg0) {
		List<Date> date = evento.getDate();
		Date dat = new Date();
		if (date != null && date.size() > 0) {
			if (dat.before(date.get(0))) {
				dat = date.get(0);
			}
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 7);
		Date nextWeek = cal.getTime();
		if (dat.after(nextWeek)) {
			String meteo = "";
			evento.setMeteo(meteo);
		} else {
			String meteo = new MeteoParser().getMeteo(evento.getLuogo(), dat);
			if (meteo == null) {
				meteo = "";
			}
			evento.setMeteo(meteo);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
	}

}
