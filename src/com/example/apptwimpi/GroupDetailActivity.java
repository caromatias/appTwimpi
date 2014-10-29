package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.apptwimpi.GroupActivity.GetGroupTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GroupDetailActivity extends Activity {

	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;

	private ListView lv1;
	private mGetGroupTask mGetGroupTask = null;
	private String[] nombreAmigos;
	private String[] idAmigos;
	private TextView txt1;
	private String tituloGrupo;
	private ProgressDialog pDialog;
	private ImageButton CreateEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		pDialog = new ProgressDialog(GroupDetailActivity.this);
        pDialog.setMessage("Cargando Detalles...");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();

		txt1 = (TextView) findViewById(R.id.txtTituloGrupo);
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
			ArrayList<String> parametros = new ArrayList<String>();
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

					idAmigos[i] = jsonObject.getString("grupo_admin");
					nombreAmigos[i] = jsonObject.getString("usuario_nombre");
					tituloGrupo = jsonObject.getString("grupo_nombre");
				}

			} catch (Exception error) {
				exito = false;
				Log.d("ERROR FATAL!!!!", "QUEDO LA CAGAAAAAAAA!!! HERMANO WOMP");
			}
			return exito;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			txt1.setText(tituloGrupo);
			lv1 = (ListView) findViewById(R.id.listView1);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getApplicationContext(), R.layout.custom_textview,
					nombreAmigos);
			lv1.setAdapter(adapter);
			registerForContextMenu(lv1);
			pDialog.dismiss();
			
			CreateEvent = (ImageButton) findViewById(R.id.imageButton1);
			CreateEvent.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(GroupDetailActivity.this,CreateEventActivity.class);
					Bundle b=new Bundle();
					b.putStringArray("IdAmigosT", idAmigos);
					i.putExtras(b);
					startActivity(i);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});
		}

		@Override
		protected void onCancelled() {

		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listView1) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			if (!idAmigos[info.position].equals(user.get(SessionManager.KEY_NAME))) {
				menu.setHeaderTitle(nombreAmigos[info.position]);
				String[] menuItems = getResources()
						.getStringArray(R.array.menu);
				for (int i = 0; i < menuItems.length; i++) {
					menu.add(Menu.NONE, i, i, menuItems[i]);
				}
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.menu);
		String menuItemName = menuItems[menuItemIndex];
		String listItemName = idAmigos[info.position];

		// TextView text = (TextView)findViewById(R.id.footer);
		// text.setText(String.format("Selected %s for item %s", menuItemName,
		// listItemName));
		return true;
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
