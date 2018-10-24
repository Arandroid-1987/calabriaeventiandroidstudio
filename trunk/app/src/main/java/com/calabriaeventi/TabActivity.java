package com.calabriaeventi;

import java.util.Locale;

import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.utils.Constants;
import com.calabriaeventi.utils.GestoreFeed;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

@SuppressLint("NewApi")
public class TabActivity extends AppCompatActivity {

    private final static int HOME = 0;
    private final static int TOMORROW = 1;
    private final static int EVENTI = 2;
    private final static int PREFERITI = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, Constants.ADMOB_APP_ID);

        ViewPager viewPager = findViewById(R.id.container);
        SectionsPagerAdapter viewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);

        tabs.setupWithViewPager(viewPager);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment newFragment;
            if (position == PREFERITI) {
                newFragment = new PreferitiFragment();
            } else {
                newFragment = GestoreFeed.choose(position);
            }
            return newFragment;
        }

        @Override
        public int getCount() {
            return PREFERITI + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case HOME:
                    return getString(R.string.oggi).toUpperCase(l);
                case TOMORROW:
                    return getString(R.string.domani).toUpperCase(l);
                case EVENTI:
                    return getString(R.string.eventi_regione).toUpperCase(l);
                case PREFERITI:
                    return getString(R.string.preferiti).toUpperCase(l);
                default:
                    return GlobalState.province[position - 3].getNome().toUpperCase(l);
            }
        }
    }

}
