package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GroupActivity extends Activity {

	private GetGroupTask mGetTask = null;

	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;
	String[] nombreAmigos;
	String[] idAmigos;
	String[] amigosSeleccionados;
	private ListView lv1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();

		lv1 = (ListView) findViewById(R.id.lista_grupos);

		mGetTask = new GetGroupTask();
		mGetTask.execute((Void) null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
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
		case R.id.create_group:
			Intent i = new Intent(GroupActivity.this, CreateGroupActivity.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public class GetGroupTask extends AsyncTask<Void, Void, Boolean> {
		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(Void... params) {

			boolean exito = false;
			ArrayList parametros = new ArrayList();
			parametros.add("GrupoAdmin");
			parametros.add(user.get(SessionManager.KEY_NAME));

			Log.d("LOG", parametros.toString());

			JSONParseoArray jParseo = new JSONParseoArray();

			String URL = "https://www.pisodigital.cl/twimpiweb/getGroup.php";

			JSONArray json = jParseo.getJSONFromUrl(URL, "post", parametros);

			try {
				nombreAmigos = new String[json.length()];
				idAmigos = new String[json.length()];
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObject = json.getJSONObject(i);
					
					nombreAmigos[i] = jsonObject.getString("grupo_nombre");
					idAmigos[i] = jsonObject.getString("grupo_id");
				}

			} catch (Exception error) {
				exito = false;
				Toast.makeText(getApplicationContext(),
						"error:" + error.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();
			}
			return exito;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			ArrayAdapter <String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_textview, nombreAmigos);
			lv1.setAdapter(adapter);
			lv1.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
	                Intent i = new Intent(GroupActivity.this, GroupDetailActivity.class);
	                i.putExtra("idGrupo", idAmigos[posicion]);
	                startActivity(i);
	            }
	        });	
		}

		@Override
		protected void onCancelled() {

		}
	}
}
