package com.calabriaeventi.core;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import android.app.Application;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.util.SparseArray;

import com.calabriaeventi.R;
import com.calabriaeventi.db.MyDatabase;
import com.calabriaeventi.net.JBlasaParser;
import com.calabriaeventi.utils.DateUtils;
import com.calabriaeventi.utils.ObservableImpl;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class GlobalState extends Application {
	private MyDatabase db;
	private ObservableImpl observable = new ObservableImpl();
	private List<Evento> eventi;
	private SparseArray<List<Evento>> eventiProvince;
	private int k;

	public void setK(int k) {
		this.k = k;
	}

	public int getK() {
		return k;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		eventi = new LinkedList<Evento>();
		eventiProvince = new SparseArray<List<Evento>>();
		db = new MyDatabase(getApplicationContext());
		db.open();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCacheExtraOptions(480, 800)
				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
				.threadPoolSize(3)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.imageDownloader(
						new BaseImageDownloader(getApplicationContext())) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.writeDebugLogs().build();

		ImageLoader.getInstance().init(config);
	}

	public void storeFavorite(Evento e) {
		db.insert(e);
	}

	public void deleteFavorite(Evento e) {
		db.remove(e);
		observable.setChanged();
		observable.notifyObservers();
	}

	public List<Evento> loadFavorites() {
		List<Evento> ris = new LinkedList<Evento>();
		Cursor c = db.fetchEntries();
		c.moveToFirst();
		if (c.getCount() > 0) {
			while (!c.isAfterLast()) {
				Evento e = new Evento();
				e.setNome(c.getString(1));
				e.setData(c.getString(2));
				e.setLuogo(c.getString(3));
				e.setDescrizione(c.getString(4));
				e.setImageUrl(c.getString(5));
				e.setMeteo(c.getString(6));
				e.setDate(DateUtils.getDatas(e.getData()));
				ris.add(e);
				c.moveToNext();
			}
		}
		return ris;
	}

	public List<Evento> getEvents() {
		if (eventi.isEmpty()) {
			eventi = new JBlasaParser().read(getString(R.string.feed_eventi),
					false, this);
		}
		return eventi;
	}

	public List<Evento> retrieveEvents(int feed) {
		if (feed == -1) {
			return eventi;
		} else {
			List<Evento> ris = eventiProvince.get(feed);
			if (ris == null) {
				ris = new LinkedList<Evento>();
				eventiProvince.put(feed, ris);
			}
			return ris;
		}
	}

	public List<Evento> getEvents(int feed) {
		if (feed == -1) {
			return getEvents();
		} else {
			List<Evento> ris = eventiProvince.get(feed);
			if (ris == null || ris.isEmpty()) {
				ris = new JBlasaParser().read(getString(feed), true, this);
				eventiProvince.put(feed, ris);
			}
			return ris;
		}
	}

	public List<Evento> getOtherEvents() {
		List<Evento> otherEvents = new JBlasaParser().readOther(
				getString(R.string.feed_eventi), eventi.size(),
				eventi.size() + 10, this);
		eventi.addAll(otherEvents);
		return otherEvents;
	}

	public Observable getObservable() {
		return observable;
	}

	public List<Evento> getEventiGiornalieri() {
		if (eventi.isEmpty()) {
			eventi = new JBlasaParser().read(getString(R.string.feed_eventi),
					false, this);
		}
		List<Evento> eventiGiornalieri = new LinkedList<Evento>();
		for (Evento evento : eventi) {
			List<Date> date = evento.getDate();
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			Date today = c.getTime();
			for (Date data : date) {
				c.setTime(data);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.MILLISECOND, 0);
				c.set(Calendar.SECOND, 0);
				Date dataTmp = c.getTime();
				if (dataTmp.equals(today)) {
					eventiGiornalieri.add(evento);
					break;
				}
			}
		}
		return eventiGiornalieri;
	}
}
