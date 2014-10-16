package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EventDetailActivity extends Activity {

	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;

	private ListView lv1;
	private mGetEventTask mGetEventTask = null;
	private mUpdateEstadoEvent mUpdateEstadoEvent = null;
	private String[] nombreAmigos;
	private String[] idAmigos;
	private TextView txt1;
	private TextView txt2;
	private String tituloEvento;
	private String descripcionEvento;
	private Button btnSi;
	private Button btnNo;
	private String idEvento;
	private String accion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();

		txt1 = (TextView) findViewById(R.id.txtTituloEvento);
		txt2 = (TextView) findViewById(R.id.txtDescripcionEvento);
		btnSi = (Button) findViewById(R.id.btn_si);
		btnNo = (Button) findViewById(R.id.btn_no);
		mGetEventTask = new mGetEventTask();
		mGetEventTask.execute((Void) null);
		
		mUpdateEstadoEvent = new mUpdateEstadoEvent();
		
		btnSi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				accion = "1";
				mUpdateEstadoEvent.execute((Void) null);
			}
		});
		btnNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				accion = "2";
				mUpdateEstadoEvent.execute((Void) null);
			}
		});
		
	}

	public class mGetEventTask extends AsyncTask<Void, Void, Boolean> {
		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(Void... params) {

			Bundle bundle = getIntent().getExtras();
			bundle.getString("id");
			boolean exito = false;
			ArrayList<String> parametros = new ArrayList<String>();
			parametros.add("idEvento");
			parametros.add(bundle.getString("idEvento"));
			idEvento = bundle.getString("idEvento");

			Log.d("LOG", parametros.toString());

			JSONParseoArray jParseo = new JSONParseoArray();

			String URL = "https://www.pisodigital.cl/twimpiweb/getEventDetails.php";

			JSONArray json = jParseo.getJSONFromUrl(URL, "post", parametros);

			try {
				nombreAmigos = new String[json.length()];
				idAmigos = new String[json.length()];
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObject = json.getJSONObject(i);

					idAmigos[i] = jsonObject.getString("evento_creador");
					nombreAmigos[i] = jsonObject.getString("usuario_nombre");
					tituloEvento = jsonObject.getString("evento_nombre");
					descripcionEvento = jsonObject.getString("evento_descripcion");
				}

			} catch (Exception error) {
				exito = false;
				Log.d("ERROR FATAL!!!!", "QUEDO LA CAGAAAAAAAA!!! HERMANO WOMP");
			}
			return exito;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			txt1.setText(tituloEvento);
			txt2.setText(descripcionEvento);
			lv1 = (ListView) findViewById(R.id.listView1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getApplicationContext(), R.layout.custom_textview,
					nombreAmigos);
			lv1.setAdapter(adapter);
			registerForContextMenu(lv1);
			btnSi.setEnabled(true);
			btnNo.setEnabled(true);
		}

		@Override
		protected void onCancelled() {

		}
	}
	
	public class mUpdateEstadoEvent extends AsyncTask<Void, Void, Boolean> {
		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(Void... params) {

			boolean exito = false;
			ArrayList<String> parametros = new ArrayList<String>();
			parametros.add("idEvento");
			parametros.add(idEvento);
			parametros.add("idUsuario");
			parametros.add(user.get(SessionManager.KEY_NAME));
			parametros.add("Accion");
			parametros.add(accion);

			Log.d("LOG", parametros.toString());

			JSONParseoArray jParseo = new JSONParseoArray();

			String URL = "https://www.pisodigital.cl/twimpiweb/updateAsistencia.php";

			JSONArray json = jParseo.getJSONFromUrl(URL, "post", parametros);

			try {
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObject = json.getJSONObject(i);
					if(jsonObject.getString("success").equals("insertado")){
						exito = true;
					}else{
						exito = false;
					}
				}

			} catch (Exception error) {
				exito = false;
				Log.d("ERROR FATAL!!!!", "QUEDO LA CAGAAAAAAAA!!! HERMANO WOMP");
			}
			return exito;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (success) {
				Log.d("BIEN IMBECIL!!!!", "FUNCIONARA LA WEA?");
			} else {
				Log.d("MAL IMBECIL!!!!", "NO FUNCIONO NA LA WEA");
			}
		}

		@Override
		protected void onCancelled() {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			// overridePendingTransition(R.anim.right_in, R.anim.right_out);
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition( R.anim.push_down_in, R.anim.push_down_out );
	}
}
