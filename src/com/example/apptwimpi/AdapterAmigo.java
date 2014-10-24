package com.example.apptwimpi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterAmigo extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<Amigo> items;
	private ImageLoader imgLoader;

	public AdapterAmigo(Activity activity, ArrayList<Amigo> items) {
		this.activity = activity;
		this.items = items;
		imgLoader=new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Generamos una convertView por motivos de eficiencia
		View v = convertView;

		// Asociamos el layout de la lista que hemos creado
		if (convertView == null) {
			LayoutInflater inf = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf.inflate(R.layout.itemlista, null);
		}

		// Creamos un objeto directivo
		final Amigo dir = items.get(position);
		////////
		//imgLoader = new ImageLoader(this);
		final ImageView foto = (ImageView) v.findViewById(R.id.foto);
		imgLoader.DisplayImage(dir.getFoto(), foto);
		//Rellenamos la fotografía
		/*
				final ImageView foto = (ImageView) v.findViewById(R.id.foto);
				AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
					protected Bitmap doInBackground(Void... p) {
						Bitmap bm = null;
						try {
							URL aURL = new URL(dir.getFoto());
							URLConnection conn = aURL.openConnection();
							conn.setUseCaches(true);
							conn.connect();
							InputStream is = conn.getInputStream();
							BufferedInputStream bis = new BufferedInputStream(is);
							bm = BitmapFactory.decodeStream(bis);
							bis.close();
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return bm;
					}

					protected void onPostExecute(Bitmap bm) {
						Drawable drawable = new BitmapDrawable(activity.getResources(), bm);
						foto.setImageDrawable(drawable);
					}
				};
				t.execute();
		*/
		TextView nombre = (TextView) v.findViewById(R.id.nombre);
		nombre.setText(dir.getNombre());
		// Rellenamos el cargo
		TextView cargo = (TextView) v.findViewById(R.id.cargo);
		cargo.setText(dir.getCargo());

		// Retornamos la vista
		return v;
	}
}
