package com.calabriaeventi;

import java.util.Locale;

import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.utils.GestoreFeed;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

@SuppressLint("NewApi")
public class TabActivity extends AppCompatActivity {

    private final static int HOME = 0;
    private final static int EVENTI = 1;
    private final static int PREFERITI = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                case EVENTI:
                    return getString(R.string.eventi_regione).toUpperCase(l);
                case PREFERITI:
                    return getString(R.string.preferiti).toUpperCase(l);
                default:
                    return GlobalState.province[position - 2].getNome().toUpperCase(l);
            }
        }
    }

}