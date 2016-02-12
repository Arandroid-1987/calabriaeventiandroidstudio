package com.calabriaeventi;

import java.util.Locale;

import com.calabriaeventi.R;
import com.calabriaeventi.ui.SlidingTabLayout;
import com.calabriaeventi.utils.GestoreFeed;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
	private final static int COSENZA = 2;
	private final static int CROTONE = 3;
	private final static int CATANZARO = 4;
	private final static int VIBO_VALENTIA = 5;
	private final static int REGGIO_CALABRIA = 6;
	private final static int PREFERITI = 7;
	
	private Toolbar toolbar;
	private ViewPager viewPager;
	private SlidingTabLayout tabs;

	private Fragment[] fragments = new Fragment[PREFERITI + 1];
	private SectionsPagerAdapter viewPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab);
		
		toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
	    setSupportActionBar(toolbar);
	    
	    TypedValue colorAccentValue = new TypedValue();
		getTheme().resolveAttribute(R.attr.colorAccent, colorAccentValue, true);
	    
	    toolbar.setTitleTextColor(colorAccentValue.data);

	    viewPager = (ViewPager) findViewById(R.id.pager);
	    viewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
	    viewPager.setAdapter(viewPagerAdapter);
	    
	    tabs = (SlidingTabLayout) findViewById(R.id.tabs);
	    tabs.setDistributeEvenly(true);
	    tabs.useTextColorAccent(false);
	    
	    tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
            	TypedValue colorPrimary = new TypedValue();
    			getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
                return colorPrimary.data;
            }
        });
	    
	    tabs.setViewPager(viewPager);
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
			// getItem is called to instantiate the fragment for the given page.
			Fragment newFragment = null;
			switch (position) {
			case HOME:
				newFragment = fragments[position];
				if (newFragment == null) {
					newFragment = new HomeFragment();
					fragments[position] = newFragment;
				}
				break;
			case EVENTI:
				newFragment = fragments[position];
				if (newFragment == null) {
					newFragment = new EventiFragment();
					fragments[position] = newFragment;
				}
				break;
			case COSENZA:
				newFragment = GestoreFeed.choose(TabActivity.this, position
						- COSENZA, -1, true);
				fragments[position] = newFragment;
				break;
			case CROTONE:
				newFragment = GestoreFeed.choose(TabActivity.this, position
						- COSENZA, -1, true);
				fragments[position] = newFragment;
				break;
			case CATANZARO:
				newFragment = GestoreFeed.choose(TabActivity.this, position
						- COSENZA, -1, true);
				fragments[position] = newFragment;
				break;
			case VIBO_VALENTIA:
				newFragment = GestoreFeed.choose(TabActivity.this, position
						- COSENZA, -1, true);
				fragments[position] = newFragment;
				break;
			case REGGIO_CALABRIA:
				newFragment = GestoreFeed.choose(TabActivity.this, position
						- COSENZA, -1, true);
				fragments[position] = newFragment;
				break;
			case PREFERITI:
				newFragment = new PreferitiFragment();
				fragments[position] = newFragment;
				break;
			default:
				break;
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
			case COSENZA:
				return getString(R.string.cosenza).toUpperCase(l);
			case CATANZARO:
				return getString(R.string.catanzaro).toUpperCase(l);
			case CROTONE:
				return getString(R.string.crotone).toUpperCase(l);
			case VIBO_VALENTIA:
				return getString(R.string.vibo_valentia).toUpperCase(l);
			case REGGIO_CALABRIA:
				return getString(R.string.reggio_calabria).toUpperCase(l);
			case PREFERITI:
				return getString(R.string.preferiti).toUpperCase(l);
			}

			return null;
		}
	}

}
