package com.calabriaeventi.ui;

import java.util.ArrayList;
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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.calabriaeventi.R;
import com.calabriaeventi.core.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.core.Loading;
import com.calabriaeventi.utils.ActionCommon;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class EventoAdapter extends BaseExpandableListAdapter {

	private int layoutID;
	private DisplayImageOptions options;
	private DisplayImageOptions optionsMeteo;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private List<Evento> items = new ArrayList<Evento>();
	private Activity context;
	private SparseArray<ViewGroupHolder> groupViews = new SparseArray<ViewGroupHolder>();

	private final static int EVENTO_NON_PREFERITO = 0;
	private final static int EVENTO_PREFERITO = 1;

	public EventoAdapter(Activity context, int textViewResourceId,
			List<Evento> objects) {
		super();
		layoutID = textViewResourceId;
		this.items = objects;
		this.context = context;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.immm)
				.showImageForEmptyUri(R.drawable.immm) // resource or drawable
				.showImageOnFail(R.drawable.immm) // resource or drawable
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.cacheOnDisc(true) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.build();
		optionsMeteo = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.unknown)
				.showImageForEmptyUri(R.drawable.unknown)
				.showImageOnFail(R.drawable.unknown) // resource or drawable
				.resetViewBeforeLoading(false) // default
				.cacheInMemory(true) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.build();
	}

	public int getCount() {
		return this.items.size();
	}

	public Evento getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public int getGroupCount() {
		return items.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		Evento evento = items.get(groupPosition);
		if (evento instanceof Loading) {
			return 0;
		}
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return items.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View row = null;
		final Evento evento = getItem(groupPosition);
		if (!(evento instanceof Loading)) {
			row = convertView;
			ViewGroupHolder holder = new ViewGroupHolder();
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(layoutID, parent, false);

				holder.nome = (TextView) row.findViewById(R.id.eventoNome);
				holder.luogo = (TextView) row.findViewById(R.id.eventoLuogo);
				holder.data = (TextView) row.findViewById(R.id.eventoData);
				holder.immagine = (ImageView) row
						.findViewById(R.id.eventoImmagine);
				holder.map = row.findViewById(R.id.map);
				holder.meteo = (ImageView) row.findViewById(R.id.meteo);
				row.setTag(holder);
				groupViews.put(groupPosition, holder);
			} else {
				holder = (ViewGroupHolder) convertView.getTag();
				groupViews.put(groupPosition, holder);
			}

			String nome = evento.getNome().trim();
			StringTokenizer st = new StringTokenizer(nome);
			StringBuilder nomeSB = new StringBuilder();
			while (st.hasMoreTokens()) {
				String token = st.nextToken().toLowerCase(Locale.getDefault());
				token = token.substring(0, 1).toUpperCase(Locale.getDefault())
						+ token.substring(1);
				nomeSB.append(token).append(" ");
			}

			if (holder == null || holder.nome == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(layoutID, parent, false);
				holder = new ViewGroupHolder();
				holder.nome = (TextView) row.findViewById(R.id.eventoNome);
				holder.luogo = (TextView) row.findViewById(R.id.eventoLuogo);
				holder.data = (TextView) row.findViewById(R.id.eventoData);
				holder.immagine = (ImageView) row
						.findViewById(R.id.eventoImmagine);
				holder.map = row.findViewById(R.id.map);
				holder.meteo = (ImageView) row.findViewById(R.id.meteo);
				row.setTag(holder);
			}

			holder.nome.setText(nomeSB.toString());
			final String luogo = evento.getLuogo().replace("Luogo: ", "")
					.trim();
			holder.luogo.setText(luogo);
			holder.data.setText(evento.getData().trim());

			holder.map.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = "https://www.google.it/maps/place/" + luogo;
					Intent intent = new Intent(
							android.content.Intent.ACTION_VIEW, Uri.parse(url));
					context.startActivity(intent);
				}
			});

			imageLoader.displayImage(evento.getImageUrl(), holder.immagine,
					options);

			imageLoader.displayImage(evento.getMeteo(), holder.meteo,
					optionsMeteo);
		} else {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.loading, parent, false);
		}
		return row;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View row = convertView;
		final ViewChildHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.evento_espanso, parent, false);

			holder = new ViewChildHolder();
			holder.descrizione = (TextView) row
					.findViewById(R.id.descrizioneTV);
			holder.share = row.findViewById(R.id.share);
			holder.preferiti = (ImageView) row.findViewById(R.id.preferiti);
			holder.calendario = row.findViewById(R.id.calendario);
			row.setTag(holder);
		} else {
			holder = (ViewChildHolder) row.getTag();
		}

		final Evento evento = getItem(groupPosition);

		holder.descrizione.setText(evento.getDescrizione().trim());

		holder.share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewGroupHolder groupHolder = groupViews.get(groupPosition);
				if (groupHolder != null) {
					Drawable img = groupHolder.immagine.getDrawable();
					if (img instanceof BitmapDrawable) {
						Bitmap bitmap = ((BitmapDrawable) img).getBitmap();
						ActionCommon.share(evento, context, bitmap);
					} else {
						ActionCommon.share(evento, context);
					}
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

		return row;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public static class ViewGroupHolder {
		TextView nome;
		TextView luogo;
		TextView data;
		ImageView immagine;
		View map;
		ImageView meteo;
	}

	public static class ViewChildHolder {
		TextView descrizione;
		View share;
		ImageView preferiti;
		View calendario;
	}
}
