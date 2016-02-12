package com.calabriaeventi.utils;

import com.calabriaeventi.R;
import com.calabriaeventi.EventiFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class GestoreFeed {
	
	private final static int COSENZA = 0;
	private final static int CROTONE = 1;
	private final static int CATANZARO = 2;
	private final static int VIBO_VALENTIA = 3;
	private final static int REGGIO_CALABRIA = 4;

	public static Fragment choose(Activity context, int position,
			int activity, boolean home) {
		Fragment newFragment = new EventiFragment();;
		Bundle bundle = new Bundle();
		switch (position) {
		case COSENZA:
			bundle.putInt(EventiFragment.FEED, R.string.feed_eventi_cosenza);
			break;
		case CROTONE:
			bundle.putInt(EventiFragment.FEED, R.string.feed_eventi_crotone);
			break;
		case CATANZARO:
			bundle.putInt(EventiFragment.FEED, R.string.feed_eventi_catanzaro);
			break;
		case VIBO_VALENTIA:
			bundle.putInt(EventiFragment.FEED, R.string.feed_eventi_vibo_valentia);
			break;
		case REGGIO_CALABRIA:
			bundle.putInt(EventiFragment.FEED, R.string.feed_eventi_reggio_calabria);
			break;
		default:
			break;
		}
		newFragment.setArguments(bundle);
		return newFragment;
	}
}
