package com.calabriaeventi.ui.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.calabriaeventi.R;
import com.calabriaeventi.glide.GlideApp;
import com.calabriaeventi.io.AsyncCallback;
import com.calabriaeventi.io.SharedPreferencesManager;
import com.calabriaeventi.model.Evento;
import com.calabriaeventi.utils.ActionCommon;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

public class EventoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, Observer {
    private TextView nome;
    private TextView luogo;
    private TextView data;
    private ImageView immagine;
    private ImageView meteo;
    private ImageView preferiti;
    private View actionMore;

    private Context context;
    private Evento evento;

    private final static int EVENTO_NON_PREFERITO = 0;
    private final static int EVENTO_PREFERITO = 1;
    private SharedPreferencesManager sharedPreferencesManager;

    public EventoViewHolder(View itemView) {
        super(itemView);

        this.nome = itemView.findViewById(R.id.eventoNome);
        this.luogo = itemView.findViewById(R.id.eventoLuogo);
        this.data = itemView.findViewById(R.id.eventoData);
        this.immagine = itemView.findViewById(R.id.eventoImmagine);
        this.meteo = itemView.findViewById(R.id.meteo);
        this.preferiti = itemView.findViewById(R.id.action_favorite);
        this.actionMore = itemView.findViewById(R.id.action_more);
    }

    public void bindToModel(final Evento evento, final Activity context) {
        this.evento = evento;
        this.context = context;
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        sharedPreferencesManager.getObservable().addObserver(this);

        String nome = evento.getNome().trim();
        StringTokenizer st = new StringTokenizer(nome);
        StringBuilder nomeSB = new StringBuilder();
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toLowerCase(Locale.getDefault());
            token = token.substring(0, 1).toUpperCase(Locale.getDefault())
                    + token.substring(1);
            nomeSB.append(token).append(" ");
        }

        this.nome.setText(nomeSB.toString());
        final String luogo = evento.getLuogo().replace("Luogo: ", "")
                .trim();
        this.luogo.setText(luogo);

        if (!evento.getDataInizio().equals(evento.getDataFine())) {
            this.data.setText(context.getString(R.string.data_da_a, evento.getDataInizio(), evento.getDataFine()));
        } else {
            this.data.setText(evento.getDataInizio());
        }

        this.luogo.setOnClickListener(this);

        if (sharedPreferencesManager.isFavorite(evento)) {
            this.preferiti.setImageResource(R.drawable.baseline_favorite_black_48);
            this.preferiti.setTag(EVENTO_PREFERITO);
        } else {
            this.preferiti.setImageResource(R.drawable.baseline_favorite_border_black_48);
            this.preferiti.setTag(EVENTO_NON_PREFERITO);
        }

        this.preferiti.setOnClickListener(this);
        this.actionMore.setOnClickListener(this);

        GlideApp.with(context).load(evento.getImageUrl()).placeholder(R.drawable.immm)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform().into(this.immagine);

        this.meteo.setImageDrawable(null);
        String meteo = evento.getMeteo(new AsyncCallback<String>() {
            @Override
            public void callback(String s) {
                initMeteo(context, s);
            }
        });

        initMeteo(context, meteo);

        itemView.setOnClickListener(this);
    }

    private void initMeteo(final Activity context, final String meteo) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (meteo == null || meteo.isEmpty()) {
                    EventoViewHolder.this.meteo.setVisibility(View.GONE);
                } else {
                    EventoViewHolder.this.meteo.setVisibility(View.VISIBLE);
                    GlideApp.with(context).load(meteo).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(EventoViewHolder.this.meteo);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.equals(itemView)) {
            // TODO: detail activity
        } else {
            int id = view.getId();
            switch (id) {
                case R.id.action_more:
                    PopupMenu popupMenu = new PopupMenu(context, actionMore);
                    popupMenu.setOnMenuItemClickListener(this);
                    popupMenu.getMenuInflater().inflate(R.menu.item_evento, popupMenu.getMenu());
                    popupMenu.show();
                    break;
                case R.id.action_favorite:
                    if (EventoViewHolder.this.preferiti.getTag() == null || EventoViewHolder.this.preferiti.getTag().equals(EVENTO_NON_PREFERITO)) {
                        sharedPreferencesManager.storeFavorite(evento);
                        EventoViewHolder.this.preferiti.setImageResource(R.drawable.baseline_favorite_black_48);
                        EventoViewHolder.this.preferiti.setTag(EVENTO_PREFERITO);
                    } else {
                        sharedPreferencesManager.deleteFavorite(evento);
                        EventoViewHolder.this.preferiti.setImageResource(R.drawable.baseline_favorite_border_black_48);
                        EventoViewHolder.this.preferiti.setTag(EVENTO_NON_PREFERITO);
                    }
                    break;
                case R.id.eventoLuogo:
                    String url = "https://www.google.it/maps/place/" + luogo;
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                    break;
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_share:
                ActionCommon.share(evento, context);
                break;
            case R.id.action_add_calendar:
                ActionCommon.addToCalendar(evento, context);
                break;
        }
        return false;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (sharedPreferencesManager.isFavorite(evento)) {
            this.preferiti.setImageResource(R.drawable.baseline_favorite_black_48);
            this.preferiti.setTag(EVENTO_PREFERITO);
        } else {
            this.preferiti.setImageResource(R.drawable.baseline_favorite_border_black_48);
            this.preferiti.setTag(EVENTO_NON_PREFERITO);
        }
    }
}
