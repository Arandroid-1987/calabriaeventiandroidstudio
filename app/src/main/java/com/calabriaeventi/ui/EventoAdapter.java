package com.calabriaeventi.ui;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.calabriaeventi.model.Evento;
import com.calabriaeventi.ui.viewholder.EventoViewHolder;

import java.util.ArrayList;

public class EventoAdapter extends RecyclerView.Adapter<EventoViewHolder> {
    private int layoutID;
    private ArrayList<Evento> unfilteredList;
    private ArrayList<Evento> filteredList;
    private Activity context;
    private LayoutInflater inflater;

    public EventoAdapter(Activity context, int textViewResourceId, ArrayList<Evento> unfilteredList) {
        super();
        this.layoutID = textViewResourceId;
        this.unfilteredList = unfilteredList;
        this.filteredList = new ArrayList<>(unfilteredList);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layoutID, parent, false);
        return new EventoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        final Evento evento = filteredList.get(position);
        holder.bindToModel(evento, context, position);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void applyFilter(String filter) {
        filteredList.clear();
        for (Evento evento : unfilteredList) {
            if (evento.isCompliant(filter)) {
                filteredList.add(evento);
            }
        }
    }
}
