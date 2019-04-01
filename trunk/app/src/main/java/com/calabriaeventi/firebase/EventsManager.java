package com.calabriaeventi.firebase;

import androidx.annotation.NonNull;

import com.calabriaeventi.io.AsyncCallback;
import com.calabriaeventi.model.Evento;
import com.calabriaeventi.utils.Constants;
import com.calabriaeventi.utils.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class EventsManager {
    private final static String PROVINCIA_COLLECTION = "provincia";
    private final static String EVENTI_COLLECTION = "eventi";

    private static EventsManager instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EventsManager() {
    }

    public static EventsManager getInstance() {
        if (instance == null) {
            instance = new EventsManager();
        }
        return instance;
    }

    public void loadEvents(String provincia, final AsyncCallback<ArrayList<Evento>> async) {
        db.collection(PROVINCIA_COLLECTION + "/" + provincia + "/" + EVENTI_COLLECTION).orderBy(Evento.START_DATE_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Evento> events = new ArrayList<>();
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        Evento evento = snapshot.toObject(Evento.class);
                        if (evento != null) {
                            DateUtils.setDates(evento);
                            events.add(evento);
                        }
                    }
                }
                async.callback(events);
            }
        });
    }

    public static ArrayList<Evento> getEventiGiornalieri(ArrayList<Evento> eventi) {
        ArrayList<Evento> eventiGiornalieri = new ArrayList<>();
        for (Evento evento : eventi) {
            ArrayList<Date> date = evento.getDate();
            for (Date data : date) {
                String dateStr = Constants.CACHE_DATE_FORMATTER.format(data);
                if (dateStr.equals(Constants.TODAY)) {
                    eventiGiornalieri.add(evento);
                    break;
                }
            }
        }
        Collections.sort(eventiGiornalieri, new Comparator<Evento>() {
            @Override
            public int compare(Evento evento, Evento other) {
                return - evento.compareTo(other);
            }
        });
        return eventiGiornalieri;
    }

    public static ArrayList<Evento> getEventiDomani(ArrayList<Evento> eventi) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrowDate = calendar.getTime();
        String TOMORROW = Constants.CACHE_DATE_FORMATTER.format(tomorrowDate);

        ArrayList<Evento> eventiDomani = new ArrayList<>();
        for (Evento evento : eventi) {
            ArrayList<Date> date = evento.getDate();
            for (Date data : date) {
                String dateStr = Constants.CACHE_DATE_FORMATTER.format(data);
                if (dateStr.equals(TOMORROW)) {
                    eventiDomani.add(evento);
                    break;
                }
            }
        }
        Collections.sort(eventiDomani, new Comparator<Evento>() {
            @Override
            public int compare(Evento evento, Evento other) {
                return - evento.compareTo(other);
            }
        });
        return eventiDomani;
    }
}
