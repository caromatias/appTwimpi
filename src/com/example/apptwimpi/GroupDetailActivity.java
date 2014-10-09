package com.example.apptwimpi;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.apptwimpi.GroupActivity.GetGroupTask;

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
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GroupDetailActivity extends Activity {

	private ListView lv1;
	private mGetGroupTask mGetGroupTask = null;
	private String[] nombreAmigos;
	private String[] idAmigos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);

		
		mGetGroupTask = new mGetGroupTask();
		mGetGroupTask.execute((Void) null);
		
	}
	
	public class mGetGroupTask extends AsyncTask<Void, Void, Boolean> {
		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(Void... params) {

			Bundle bundle = getIntent().getExtras();
			bundle.getString("id");
			boolean exito = false;
			ArrayList parametros = new ArrayList();
			parametros.add("idGrupo");
			parametros.add(bundle.getString("idGrupo"));

			Log.d("LOG", parametros.toString());

			JSONParseoArray jParseo = new JSONParseoArray();

			String URL = "https://www.pisodigital.cl/twimpiweb/getGroupDetails.php";

			JSONArray json = jParseo.getJSONFromUrl(URL, "post", parametros);

			try {
				nombreAmigos = new String[json.length()];
				idAmigos = new String[json.length()];
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObject = json.getJSONObject(i);
					
					idAmigos[i] = jsonObject.getString("id_integrante");
					nombreAmigos[i] = jsonObject.getString("usuario_nombre");
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

			lv1=(ListView)findViewById(R.id.listView1);
			ArrayAdapter <String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, nombreAmigos);
			lv1.setAdapter(adapter);
			registerForContextMenu(lv1);
			
		}

		@Override
		protected void onCancelled() {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_detail, menu);
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
}
