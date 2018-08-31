package com.calabriaeventi.ui.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.calabriaeventi.EventDetailActivity;
import com.calabriaeventi.EventDetailBottomSheet;
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

public class EventoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Observer {
    private TextView nome;
    private TextView luogo;
    private TextView data;
    private ImageView immagine;
    private ImageView meteo;
    private ImageView preferiti;
    private View actionMore;

    private AppCompatActivity context;
    private Evento evento;

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

    public void bindToModel(final Evento evento, final Activity context, final int position) {
        this.evento = evento;
        this.context = (AppCompatActivity) context;
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        sharedPreferencesManager.getObservable().addObserver(this);

        String nome = evento.getTitle().trim();
        StringTokenizer st = new StringTokenizer(nome);
        StringBuilder nomeSB = new StringBuilder();
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toLowerCase(Locale.getDefault());
            token = token.substring(0, 1).toUpperCase(Locale.getDefault())
                    + token.substring(1);
            nomeSB.append(token).append(" ");
        }

        this.nome.setText(nomeSB.toString());
        final String luogo = evento.getPlace().replace("Luogo: ", "")
                .trim();
        this.luogo.setText(luogo);

        if (!evento.getStartDate().equals(evento.getEndDate())) {
            this.data.setText(context.getString(R.string.data_da_a, evento.getStartDate(), evento.getEndDate()));
        } else {
            this.data.setText(evento.getStartDate());
        }

        this.luogo.setOnClickListener(this);

        if (sharedPreferencesManager.isFavorite(evento)) {
            this.preferiti.setImageResource(R.drawable.baseline_favorite_black_48);
        } else {
            this.preferiti.setImageResource(R.drawable.baseline_favorite_border_black_48);
        }

        this.preferiti.setOnClickListener(this);
        this.actionMore.setOnClickListener(this);

        GlideApp.with(context).load(evento.getImgUrl()).placeholder(R.drawable.immm)
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

        CardView.LayoutParams params = (CardView.LayoutParams) this.immagine.getLayoutParams();
        if (position % 5 == 0) {
            params.height = ActionCommon.pixel(context, 250);
        } else {
            params.height = ActionCommon.pixel(context, 150);
        }
        this.immagine.setLayoutParams(params);
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

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View view) {
        if (view.equals(itemView)) {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EVENT_EXTRA, evento);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                Pair<View, String> pair1 = Pair.create((View) immagine, immagine.getTransitionName());

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, pair1);
                context.startActivity(intent, options.toBundle());
            } else {
                context.startActivity(intent);
            }
        } else {
            int id = view.getId();
            switch (id) {
                case R.id.action_more:
                    EventDetailBottomSheet.newInstance(evento).show(context.getSupportFragmentManager(), "dialog");
                    break;
                case R.id.action_favorite:
                    if (!sharedPreferencesManager.isFavorite(evento)) {
                        sharedPreferencesManager.storeFavorite(evento);
                        preferiti.setImageResource(R.drawable.baseline_favorite_black_48);
                    } else {
                        sharedPreferencesManager.deleteFavorite(evento);
                        preferiti.setImageResource(R.drawable.baseline_favorite_border_black_48);
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
    public void update(Observable observable, Object o) {
        if (sharedPreferencesManager.isFavorite(evento)) {
            preferiti.setImageResource(R.drawable.baseline_favorite_black_48);
        } else {
            preferiti.setImageResource(R.drawable.baseline_favorite_border_black_48);
        }
    }
}
