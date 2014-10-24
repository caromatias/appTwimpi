package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.apptwimpi.GroupActivity.GetGroupTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsActivity extends Activity implements OnRefreshListener{

	private SwipeRefreshLayout swipeLayout;
	
	private GetGroupTask mGetTask = null;

	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;
	String[] nombres;
	String[] descripcion;
	String[] picPerfil;
	private ListView lv1;
    ArrayAdapter <String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
		
        session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();
		
		lv1 = (ListView) findViewById(R.id.lista_amigos);
		
		mGetTask = new GetGroupTask();
		mGetTask.execute((Void) null);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		getMenuInflater().inflate(R.menu.friends, menu);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		 SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		
		 searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
         searchView.setIconifiedByDefault(true);
		
         SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() 
	        {
	            @Override
	            public boolean onQueryTextChange(String newText) 
	            {
	                // this is your adapter that will be filtered
	                adapter.getFilter().filter(newText);
	                System.out.println("on text chnge text: "+newText);
	                return true;
	            }
	            @Override
	            public boolean onQueryTextSubmit(String query) 
	            {
	                // this is your adapter that will be filtered
	                adapter.getFilter().filter(query);
	                System.out.println("on query submit: "+query);
	                return true;
	            }
	        };
	        searchView.setOnQueryTextListener(textChangeListener);

	        return super.onCreateOptionsMenu(menu);
         
         
         
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
	public void onRefresh() {
		// TODO Auto-generated method stub
		mGetTask = new GetGroupTask();
		mGetTask.execute((Void) null);
		
	}
	
	public class GetGroupTask extends AsyncTask<Void, Void, Boolean> {
		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(Void... params) {

			boolean exito = false;
			ArrayList<String> parametros = new ArrayList<String>();
			parametros.add("GrupoAdmin");
			parametros.add(user.get(SessionManager.KEY_NAME));

			Log.d("LOG", parametros.toString());

			JSONParseoArray jParseo = new JSONParseoArray();

			String URL = "https://www.pisodigital.cl/twimpiweb/getAllUser.php";

			JSONArray json = jParseo.getJSONFromUrl(URL, "post", parametros);

			try {
				nombres = new String[json.length()];
				descripcion = new String[json.length()];
				picPerfil= new String[json.length()];
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObject = json.getJSONObject(i);
					
					nombres[i] = jsonObject.getString("usuario_nombre");
					descripcion[i] = jsonObject.getString("usuario_biografia");
					picPerfil[i] = jsonObject.getString("usuario_url_large");
					
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

			adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_textview, nombres);
			lv1.setAdapter(adapter);
			lv1.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
	                Intent i = new Intent(FriendsActivity.this, ProfileActivity.class);
	                i.putExtra("Biografia", descripcion[posicion]);
	                i.putExtra("nombrePerfil", nombres [posicion]); 
	                i.putExtra("fotografia", picPerfil[posicion]);
	                startActivity(i);
	            }
	            
	        });	
			swipeLayout.setRefreshing(false);
			
			lv1.setTextFilterEnabled(true);
			
		}
	
	}
	
}
