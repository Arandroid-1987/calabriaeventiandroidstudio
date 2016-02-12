package com.calabriaeventi.db;

import com.calabriaeventi.core.Evento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase {

	SQLiteDatabase mDb;
	DbHelper mDbHelper;
	Context mContext;
	private static final String DB_NAME = "calabriaEventi";// nome del db
	private static final int DB_VERSION = 6; // numero di versione del nostro db

	public MyDatabase(Context ctx) {
		mContext = ctx;
		mDbHelper = new DbHelper(ctx, DB_NAME, null, DB_VERSION);
	}

	public void open() { // il database su cui agiamo è leggibile/scrivibile
		mDb = mDbHelper.getWritableDatabase();

	}

	public void close() { // chiudiamo il database su cui agiamo
		mDb.close();
	}

	public void insert(Evento e) {
		try {
			Cursor c = mDb.query(EventMetaData.EVENT_TABLE, null,
					EventMetaData.EVENT_NAME + "=\"" + e.getNome() + "\"",
					null, null, null, null);
			if (c.getCount() > 0)
				return;
			ContentValues cv = new ContentValues();
			cv.put(EventMetaData.EVENT_NAME, e.getNome());
			cv.put(EventMetaData.EVENT_DATE, e.getData());
			cv.put(EventMetaData.EVENT_LOCATION, e.getLuogo());
			cv.put(EventMetaData.EVENT_DESCRIPTION, e.getDescrizione());
			cv.put(EventMetaData.EVENT_IMAGE_URL, e.getImageUrl());
			cv.put(EventMetaData.EVENT_METEO, e.getMeteo());
			mDb.insert(EventMetaData.EVENT_TABLE, null, cv);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void clear() {
		// mContext.deleteDatabase(DB_NAME);
		// open();
		mDb.delete(EventMetaData.EVENT_TABLE, null, null);
	}

	public void remove(Evento e) {
		mDb.delete(EventMetaData.EVENT_TABLE, EventMetaData.EVENT_NAME + "=\""
				+ e.getNome() + "\"", null);
	}

	public Cursor fetchEntries() { // metodo per fare la query di tutti i dati
		return mDb.query(EventMetaData.EVENT_TABLE, null, null, null, null,
				null, null);
	}

	public static interface EventMetaData {
		String EVENT_TABLE = "events";
		String EVENT_NAME = "name";
		String EVENT_DATE = "date";
		String EVENT_LOCATION = "location";
		String EVENT_IMAGE_URL = "image";
		String EVENT_DESCRIPTION = "description";
		String EVENT_METEO = "meteo";
		String ID = "_id";
	}

	private static final String EVENT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ EventMetaData.EVENT_TABLE
			+ " ("
			+ EventMetaData.ID
			+ " integer, "
			+ EventMetaData.EVENT_NAME
			+ " text primary key not null, "
			+ EventMetaData.EVENT_DATE
			+ " text not null, "
			+ EventMetaData.EVENT_LOCATION
			+ " text not null, "
			+ EventMetaData.EVENT_DESCRIPTION
			+ " text not null, "
			+ EventMetaData.EVENT_IMAGE_URL
			+ " text not null, "
			+ EventMetaData.EVENT_METEO
			+ " text not null " + ");";

	private class DbHelper extends SQLiteOpenHelper { // classe che ci aiuta
														// nella creazione del
														// db

		public DbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) { // solo quando il db viene
													// creato, creiamo la
													// tabella
			_db.execSQL(EVENT_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			// qui mettiamo eventuali modifiche al db, se nella nostra nuova
			// versione della app, il db cambia numero di versione
			if (oldVersion == 1 && newVersion == 2) {
				_db.execSQL("DROP TABLE " + EventMetaData.EVENT_TABLE + ";");
			}
			else if (oldVersion == 1 && newVersion == 3) {
				_db.execSQL("DROP TABLE " + EventMetaData.EVENT_TABLE + ";");
				_db.execSQL(EVENT_TABLE_CREATE);
			}
			else if(oldVersion == 2 && newVersion == 3){
				_db.execSQL(EVENT_TABLE_CREATE);
			}
			else if(newVersion >= 5){
				_db.execSQL("DROP TABLE " + EventMetaData.EVENT_TABLE + ";");
				_db.execSQL(EVENT_TABLE_CREATE);
			}
		}

	}

}