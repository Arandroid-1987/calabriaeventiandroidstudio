package com.calabriaeventi.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import com.calabriaeventi.R;
import com.calabriaeventi.core.Evento;

@SuppressLint("InlinedApi")
public class ActionCommon {

	public static void share(final Evento evento, final Context context,
			final Bitmap icon) {
		String[] s = { evento.getNome(), evento.getLuogo(), evento.getData(),
				evento.getDescrizione() };
		String title = s[0]
				+ "\n"
				+ s[1]
				+ "\n"
				+ s[2]
				+ "\n"
				+ "Powered by CalabriaEventi for Android https://play.google.com/store/apps/details?id=com.calabriaeventi&hl=it";

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.setType("image/jpeg");
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			File f = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "temporary_file.jpg");

			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.close();
			sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
		}

		sendIntent.putExtra(Intent.EXTRA_TEXT, title);
		context.startActivity(Intent.createChooser(sendIntent, context
				.getResources().getText(R.string.condividi)));
	}

	public static void share(final Evento evento, final Context context) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				String[] s = { evento.getNome(), evento.getLuogo(),
						evento.getData(), evento.getDescrizione() };
				String title = s[0]
						+ "\n"
						+ s[1]
						+ "\n"
						+ s[2]
						+ "\n"
						+ "Powered by CalabriaEventi for Android https://play.google.com/store/apps/details?id=com.calabriaeventi&hl=it";

				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.setType("image/jpeg");
				try {
					URL url = new URL(evento.getImageUrl());
					Bitmap icon = BitmapFactory.decodeStream(url
							.openConnection().getInputStream());

					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
					File f = new File(Environment.getExternalStorageDirectory()
							+ File.separator + "temporary_file.jpg");

					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.close();
					sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
				} catch (IOException e) {
					e.printStackTrace();
				}

				sendIntent.putExtra(Intent.EXTRA_TEXT, title);
				context.startActivity(Intent.createChooser(sendIntent, context
						.getResources().getText(R.string.condividi)));
				return null;
			}
		};
		task.execute();
	}

	public static void addToCalendar(Evento evento, Context context) {
		List<Date> dateEvento = evento.getDate();
		Date inizio;
		Date fine;
		if (dateEvento == null || dateEvento.isEmpty()) {
			inizio = new Date();
			fine = new Date();
		} else {
			inizio = dateEvento.get(0);
			fine = dateEvento.get(dateEvento.size() - 1);
		}
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		cal.setTime(inizio);
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				cal.getTimeInMillis());
		if (cal.get(Calendar.HOUR_OF_DAY) == 0) {
			intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
		}
		cal.setTime(fine);
		intent.putExtra(Events.EVENT_LOCATION, evento.getLuogo());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				cal.getTimeInMillis());
		intent.putExtra(Events.TITLE, evento.getNome());
		intent.putExtra(Events.DESCRIPTION, evento.getDescrizione());
		context.startActivity(intent);
	}

}
