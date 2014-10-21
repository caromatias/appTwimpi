package com.example.apptwimpi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterEvento extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<AmigoEvento> items;

	public AdapterEvento(Activity activity, ArrayList<AmigoEvento> items) {
		this.activity = activity;
		this.items = items;
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
			v = inf.inflate(R.layout.itemlista_evento, null);
		}

		// Creamos un objeto directivo
		final AmigoEvento dir = items.get(position);
		//Rellenamos la fotografía
		ImageView foto = (ImageView) v.findViewById(R.id.foto_ev);

        foto.setImageDrawable(dir.getFoto());
		TextView nombre = (TextView) v.findViewById(R.id.nombre);
		nombre.setText(dir.getNombre());

		// Retornamos la vista
		return v;
	}
}