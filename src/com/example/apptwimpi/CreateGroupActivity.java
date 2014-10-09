package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.apptwimpi.EventoActivity.CreateEventTask;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CreateGroupActivity extends ListActivity {

	private CreateGroupTask mCreateTask = null;
	
	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;
	String[] nombreAmigos;
	String[] idAmigos;
	String[] amigosSeleccionados;
	private EditText EditNombre;
	private String Nombre;
	private String asistentes = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();

		ListView lview = getListView();
		lview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lview.setTextFilterEnabled(true);

		String fqlQuery = "select uid, name, pic_square, is_app_user from user where uid in (select uid2 from friend where uid1 = me())";
		Bundle params = new Bundle();
		Session session = Session.getActiveSession();
		params.putString("q", fqlQuery);
		params.putString("access_token", user.get(SessionManager.KEY_EMAIL));

		Request request = new Request(session, "/fql", params, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						Log.i("AMIGOS FQL",
								"Got results: " + response.toString());
						try {
							GraphObject go = response.getGraphObject();
							JSONObject jso = go.getInnerJSONObject();
							JSONArray arr = jso.getJSONArray("data");

							nombreAmigos = new String[arr.length()];
							idAmigos = new String[arr.length()];
							for (int i = 0; i < (arr.length()); i++) {
								JSONObject json_obj = arr.getJSONObject(i);
								Log.i("SOSO", json_obj.getString("name"));
								// Arrays.fill(nombreAmigos,
								// json_obj.getString("name"));
								nombreAmigos[i] = json_obj.getString("name");
								idAmigos[i] = json_obj.getString("uid");
							}
							/*
							 * for(String log : nombreAmigos) {
							 * Log.v("FREEDOM",log); }
							 */
							setListAdapter(new ArrayAdapter<String>(
									CreateGroupActivity.this,
									android.R.layout.simple_list_item_multiple_choice,
									nombreAmigos));

							// nombreAmigos =
							// getResources().getStringArray(R.array.nav_options);

						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				});
		Request.executeBatchAsync(request);
	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		// ---toggle the check displayed next to the item---
		String s = "";
		asistentes = "";
		int len = parent.getCount();
		SparseBooleanArray checked = parent.getCheckedItemPositions();

		for (int i = 0; i < len; i++) {
			if (checked.get(i)) {
				String item = idAmigos[i];
				s = s + " " + item;
				asistentes = asistentes + "" + item+";";
				/* do whatever you want with the checked item */
			}
		}
		Toast.makeText(this, "Amigos selecionados- " + asistentes, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_group, menu);
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
			mCreateTask = new CreateGroupTask();
			mCreateTask.execute((Void) null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class CreateGroupTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			/*
			 * try { // Simulate network access. Thread.sleep(2000);
			 * 
			 * } catch (InterruptedException e) { return false; }
			 */
			boolean exito = false;
			EditNombre = (EditText)findViewById(R.id.edt_create_group);
			Nombre = EditNombre.getText().toString();
			ArrayList parametros = new ArrayList();
			parametros.add("NombreGrupo");
			parametros.add(Nombre);
			parametros.add("GrupoAdmin");
			parametros.add(user.get(SessionManager.KEY_NAME));
			parametros.add("Asistentes");
			parametros.add(asistentes);
			
			Log.d("LOG", parametros.toString());

			JSONParseo jParseo = new JSONParseo();

			String URL = "http://www.pisodigital.cl/twimpiweb/createGroup.php";

			JSONObject json = jParseo.recibir(URL, "post", parametros);

			try {
				String success = json.getString("success");
				Log.e("LOG", json.getString("success"));
				if (success.equals("0")) {
					exito = true;
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

			if (success) {
				// finish();
				//showProgress(false);
			} else {
				//showProgress(false);
			}
		}

		@Override
		protected void onCancelled() {
			
		}
	}
}
