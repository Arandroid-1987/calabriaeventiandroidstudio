package com.calabriaeventi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import com.calabriaeventi.R;
import com.calabriaeventi.model.Evento;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressLint("InlinedApi")
public class ActionCommon {

    public static void share(final Evento evento, final Context context) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String title = "Vorrei consigliarti un evento!" + "\n\n" + evento.getNome()
                        + "\n\n"
                        + evento.getLuogo()
                        + "\n";

                if (evento.getDataInizio().equals(evento.getDataFine())) {
                    title += evento.getDataInizio();
                } else {
                    title += evento.getDataInizio() + " - " + evento.getDataFine();
                }
                title += "\n\nPowered by CalabriaEventi for Android https://play.google.com/store/apps/details?id=com.calabriaeventi&hl=it";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");

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
