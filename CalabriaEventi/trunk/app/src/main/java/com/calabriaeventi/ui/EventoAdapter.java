package com.calabriaeventi.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.calabriaeventi.model.Evento;
import com.calabriaeventi.ui.viewholder.EventoViewHolder;

import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoViewHolder> {
    private int layoutID;
    private List<Evento> items;
    private Activity context;
    private LayoutInflater inflater;

    public EventoAdapter(Activity context, int textViewResourceId, List<Evento> objects) {
        super();
        this.layoutID = textViewResourceId;
        this.items = objects;
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
        final Evento evento = items.get(position);
        holder.bindToModel(evento, context);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
