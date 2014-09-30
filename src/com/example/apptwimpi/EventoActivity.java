package com.example.apptwimpi;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class EventoActivity extends ListActivity {

	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;
	String[] nombreAmigos;
	String[] idAmigos;
	String[] amigosSeleccionados;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_evento);
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

							// ListView lista = (ListView)
							// findViewById(R.id.list_amigos);
							// lista.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
							/*
							 * ListView lview = getListView();
							 * lview.setChoiceMode
							 * (ListView.CHOICE_MODE_MULTIPLE);
							 * lview.setTextFilterEnabled(true);
							 * ArrayList<Amigo> arraydir = new
							 * ArrayList<Amigo>(); Amigo directivo;
							 * 
							 * 
							 * for (int i = 0; i < (arr.length()); i++) {
							 * JSONObject json_obj = arr.getJSONObject(i);
							 * 
							 * directivo = new Amigo(json_obj
							 * .getString("pic_square"), json_obj
							 * .getString("name"), "Presidenta");
							 * arraydir.add(directivo); Log.i("SOSO",
							 * json_obj.getString("name"));
							 * 
							 * }
							 * 
							 * // Creo el adapter personalizado AdapterAmigo
							 * adapter = new AdapterAmigo( EventoActivity.this,
							 * arraydir);
							 * 
							 * // Lo aplico lview.setAdapter(adapter);
							 */
							nombreAmigos=new String[arr.length()];
							idAmigos=new String[arr.length()];
							for (int i = 0; i < (arr.length()); i++) {
								JSONObject json_obj = arr.getJSONObject(i);
								Log.i("SOSO",json_obj.getString("name"));
								//Arrays.fill(nombreAmigos, json_obj.getString("name"));
								nombreAmigos[i] = json_obj.getString("name");
								idAmigos[i] = json_obj.getString("uid");
							}
							/*
							for(String log : nombreAmigos)
							{
							  Log.v("FREEDOM",log);
							}
							*/
							setListAdapter(new ArrayAdapter<String>(EventoActivity.this,android.R.layout.simple_list_item_multiple_choice,nombreAmigos));

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
		int len = parent.getCount();
		SparseBooleanArray checked = parent.getCheckedItemPositions();

		for (int i = 0; i < len; i++) {
			if (checked.get(i)) {
				String item = idAmigos[i];
				s = s + " " + item;
				/* do whatever you want with the checked item */
			}
		}
		Toast.makeText(this, "Amigos selecionados- " + s, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.evento, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
