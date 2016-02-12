package com.calabriaeventi.ui;

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.calabriaeventi.R;
import com.calabriaeventi.core.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.utils.ActionCommon;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class EventoGridAdapter extends BaseAdapter {
	private List<Evento> items;
	private Activity context;
	private DisplayImageOptions options;
	private DisplayImageOptions optionsMeteo;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private final static int EVENTO_NON_PREFERITO = 0;
	private final static int EVENTO_PREFERITO = 1;

	public EventoGridAdapter(Activity context, List<Evento> objects) {
		this.items = objects;
		this.context = context;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.immm)
				.showImageForEmptyUri(R.drawable.immm) // resource or drawable
				.showImageOnFail(R.drawable.immm) // resource or drawable
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.cacheOnDisc(true) // default
				.imageScaleType(ImageScaleType.EXACTLY) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.build();
		optionsMeteo = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.unknown)
				.showImageForEmptyUri(R.drawable.unknown) // resource or
															// drawable
				.showImageOnFail(R.drawable.unknown) // resource or drawable
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.build();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		final ViewGroupHolder holder;
		if (convertView == null) {
			holder = new ViewGroupHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.evento_home, parent, false);

			holder.nome = (TextView) row.findViewById(R.id.eventoNome);
			holder.luogo = (TextView) row.findViewById(R.id.eventoLuogo);
			holder.data = (TextView) row.findViewById(R.id.eventoData);
			holder.immagine = (ImageView) row.findViewById(R.id.eventoImmagine);
			holder.map = row.findViewById(R.id.map);
			holder.share = row.findViewById(R.id.share);
			holder.preferiti = (ImageView) row.findViewById(R.id.preferiti);
			holder.calendario = row.findViewById(R.id.calendario);
			holder.meteo = (ImageView) row.findViewById(R.id.meteo);
			row.setTag(holder);
		} else {
			holder = (ViewGroupHolder) convertView.getTag();
		}

		final Evento evento = items.get(position);

		String nome = evento.getNome().trim();
		StringTokenizer st = new StringTokenizer(nome);
		StringBuilder nomeSB = new StringBuilder();
		while (st.hasMoreTokens()) {
			String token = st.nextToken().toLowerCase(Locale.getDefault());
			token = token.substring(0, 1).toUpperCase(Locale.getDefault())
					+ token.substring(1);
			nomeSB.append(token).append(" ");
		}

		holder.nome.setText(nomeSB.toString());
		final String luogo = evento.getLuogo().replace("Luogo: ", "").trim();
		holder.luogo.setText(luogo);
		holder.data.setText(evento.getData().trim());

		holder.map.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "https://www.google.it/maps/place/" + luogo;
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(url));
				context.startActivity(intent);
			}
		});

		imageLoader
				.displayImage(evento.getImageUrl(), holder.immagine, options);

		holder.share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Drawable img = holder.immagine.getDrawable();
				if (img instanceof BitmapDrawable) {
					Bitmap bitmap = ((BitmapDrawable) img).getBitmap();
					ActionCommon.share(evento, context, bitmap);
				} else {
					ActionCommon.share(evento, context);
				}
			}
		});

		GlobalState gs = (GlobalState) context.getApplication();
		List<Evento> preferiti = gs.loadFavorites();
		if (preferiti.contains(evento)) {
			holder.preferiti.setImageResource(R.drawable.fav_remove);
			holder.preferiti.setTag(EVENTO_PREFERITO);
		} else {
			holder.preferiti.setImageResource(R.drawable.fav_add);
			holder.preferiti.setTag(EVENTO_NON_PREFERITO);
		}

		holder.preferiti.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (holder.preferiti.getTag() == null
						|| holder.preferiti.getTag().equals(
								EVENTO_NON_PREFERITO)) {
					GlobalState gs = (GlobalState) context.getApplication();
					gs.storeFavorite(evento);
					holder.preferiti.setImageResource(R.drawable.fav_remove);
					holder.preferiti.setTag(EVENTO_PREFERITO);
				} else {
					GlobalState gs = (GlobalState) context.getApplication();
					gs.deleteFavorite(evento);
					holder.preferiti.setImageResource(R.drawable.fav_add);
					holder.preferiti.setTag(EVENTO_NON_PREFERITO);
				}
			}
		});

		holder.calendario.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ActionCommon.addToCalendar(evento, context);
			}
		});

		imageLoader.displayImage(evento.getMeteo(), holder.meteo, optionsMeteo);

		return row;
	}

	public static class ViewGroupHolder {
		TextView nome;
		TextView luogo;
		TextView data;
		ImageView immagine;
		View map;
		View share;
		ImageView preferiti;
		View calendario;
		ImageView meteo;
	}

}
