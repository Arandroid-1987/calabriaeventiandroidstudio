package com.calabriaeventi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.calabriaeventi.glide.GlideApp;
import com.calabriaeventi.io.SharedPreferencesManager;
import com.calabriaeventi.model.Evento;
import com.calabriaeventi.utils.ActionCommon;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.util.Pair;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EVENT_EXTRA = "EVENT_EXTRA";
    private SharedPreferencesManager sharedPreferencesManager;
    private FloatingActionButton fab;
    private Evento evento;
    private ImageView eventImg;
    private CollapsingToolbarLayout toolbarLayout;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent thisIntent = getIntent();
        evento = (Evento) thisIntent.getSerializableExtra(EVENT_EXTRA);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(evento.getTitle());
        }

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        toolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbarLayout.setTitle(evento.getTitle());
                    isShow = true;
                } else if (isShow) {
                    toolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        fab = findViewById(R.id.fab);
        eventImg = findViewById(R.id.eventoImmagine);
        eventImg.setOnClickListener(this);
        findViewById(R.id.event_location_layout).setOnClickListener(this);

        TextView eventTitle = findViewById(R.id.event_title);
        eventTitle.setText(evento.getTitle());

        TextView eventLocation = findViewById(R.id.event_location);
        eventLocation.setText(evento.getPlace());

        TextView eventDate = findViewById(R.id.event_date);
        if (!evento.getStartDate().equals(evento.getEndDate())) {
            eventDate.setText(getString(R.string.data_da_a, evento.getStartDate(), evento.getEndDate()));
        } else {
            eventDate.setText(evento.getStartDate());
        }

        TextView eventDescription = findViewById(R.id.event_description);
        eventDescription.setText(evento.getDescription());
        Linkify.addLinks(eventDescription, Linkify.ALL);

        GlideApp.with(this).load(evento.getImgUrl()).placeholder(R.drawable.immm)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform().into(eventImg);

        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        if (sharedPreferencesManager.isFavorite(evento)) {
            fab.setImageResource(R.drawable.baseline_favorite_white_48);
        } else {
            fab.setImageResource(R.drawable.baseline_favorite_border_white_48);
        }

        fab.setOnClickListener(this);
        findViewById(R.id.event_date_add_calendar).setOnClickListener(this);
        findViewById(R.id.event_location_open_map).setOnClickListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View view) {
        Intent intent;
        int id = view.getId();
        switch (id) {
            case R.id.fab:
                if (!sharedPreferencesManager.isFavorite(evento)) {
                    sharedPreferencesManager.storeFavorite(evento);
                    fab.setImageResource(R.drawable.baseline_favorite_white_48);
                } else {
                    sharedPreferencesManager.deleteFavorite(evento);
                    fab.setImageResource(R.drawable.baseline_favorite_border_white_48);
                }
                break;
            case R.id.eventoImmagine:
                intent = new Intent(this, ImageActivity.class);
                intent.putExtra(ImageActivity.EVENT_EXTRA, evento);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                    Pair<View, String> pair1 = Pair.create((View) eventImg, eventImg.getTransitionName());

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case R.id.event_location_layout:
                openMap();
                break;
            case R.id.event_date_add_calendar:
                ActionCommon.addToCalendar(evento, this);
                break;
            case R.id.event_location_open_map:
                openMap();
                break;
        }
    }

    private void openMap() {
        Intent intent;
        String url = "https://www.google.it/maps/place/" + evento.getPlace();
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_evento, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        if (fab != null) {
            fab.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean res = false;
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (fab != null) {
                    fab.setVisibility(View.GONE);
                }
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (upIntent != null && NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }
                res = true;
                break;
            case R.id.action_more:
                EventDetailBottomSheet.newInstance(evento).show(getSupportFragmentManager(), "dialog");
                break;
        }
        return res;
    }
}
