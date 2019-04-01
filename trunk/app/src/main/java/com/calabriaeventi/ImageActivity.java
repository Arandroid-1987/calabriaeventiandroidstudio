package com.calabriaeventi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.calabriaeventi.glide.GlideApp;
import com.calabriaeventi.model.Evento;

public class ImageActivity extends AppCompatActivity {
    public static final String EVENT_EXTRA = "EVENT_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent thisIntent = getIntent();
        Evento evento = (Evento) thisIntent.getSerializableExtra(EVENT_EXTRA);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(evento.getTitle());
        }

        ImageView eventImg = findViewById(R.id.image);

        GlideApp.with(this).load(evento.getImgUrl()).placeholder(R.drawable.immm)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform().into(eventImg);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        boolean res = false;
        int id = item.getItemId();
        if (id == android.R.id.home) {
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
        }
        return res;
    }
}
